package com.example.hoi4translation.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hoi4translation.common.Constants;
import com.example.hoi4translation.service.IBaseService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

/**
 * Excel工具类
 */
@Slf4j
public class ExcelUtils {
    /**
     * 导出Excel
     *
     * @param response 响应对象
     * @param service  服务实现类
     * @param entity   导出数据查询条件
     * @param clazz    导出数据实体类
     * @param <T>      导出数据实体类泛型
     */
    public static <T> void exportExcel(HttpServletResponse response, IBaseService<T> service, T entity, Class<T> clazz, ThreadPoolTaskExecutor executor) {
        // TODO 进一步优化策略：附件存储至MinIO，返回附件URL，将查询参数存储至Redis，value为附件URL
        long start = System.currentTimeMillis();

        String fileName;
        String sheetName;

        if (clazz.isAnnotationPresent(Schema.class)) {
            Schema schema = clazz.getAnnotation(Schema.class);
            String description = schema.description();
            fileName = description;
            sheetName = description;
        } else {
            fileName = "文件";
            sheetName = "工作簿";
        }

        fileName += DateUtil.now().replaceAll("[-:]", "_");

        int dataCount = service.getWrapper(entity).count().intValue();
        int fileCount = (int) Math.ceil((double) dataCount / Constants.PER_FILE_ROW_COUNT);
        int lastFileSheetCount = ((dataCount - (fileCount - 1) * Constants.PER_FILE_ROW_COUNT) / Constants.PER_FILE_ROW_COUNT) + 1;

        String finalFileName = fileName;
        List<CompletableFuture<Path>> futures = IntStream.range(0, fileCount)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                                    String tempFileName = finalFileName + "_" + i + ".xlsx";
                                    Path tempFilePath = Paths.get(System.getProperty("java.io.tmpdir"), tempFileName);
                                    generateExcelFile(tempFilePath.toString(), sheetName, service, entity, clazz, i, i == fileCount - 1 ? lastFileSheetCount : Constants.PER_FILE_SHEET_COUNT);
                                    return tempFilePath;
                                }, executor)
                                .exceptionally(e -> processException(finalFileName, sheetName, e))
                )
                .toList();

        List<Path> pathList = futures.stream()
                .map(ExcelUtils::handleFuture)
                .filter(ObjectUtil::isNotNull)
                .toList();

        if (pathList.size() == 1) {
            // 单文件
            Path singleFile = pathList.getFirst();
            try (InputStream is = Files.newInputStream(singleFile)) {
                writeResponse(response, singleFile.getFileName().toString(), is);
            } catch (IOException e) {
                log.error("单文件下载失败：{}", e.toString());
            } finally {
                FileUtil.del(singleFile);
            }
        } else {
            String zipFileName = fileName + ".zip";
            Path zipFilePath = Paths.get(System.getProperty("java.io.tmpdir"), zipFileName);
            File[] files = pathList.stream().map(Path::toFile).toArray(File[]::new);
            ZipUtil.zip(zipFilePath.toFile(), false, files);
            // 多文件ZIP
            try (InputStream is = Files.newInputStream(zipFilePath)) {
                writeResponse(response, zipFileName, is);
            } catch (IOException e) {
                log.error("多文件下载失败：{}", e.toString());
            } finally {
                FileUtil.del(zipFilePath);
                pathList.forEach(FileUtil::del);
            }
        }

        long end = System.currentTimeMillis();
        log.info("导出【{}】耗时：{}秒", fileName, (end - start) * 1.0 / 1000);
    }

    /**
     * @param pathName   文件路径
     * @param sheetName  工作簿名称
     * @param service    服务实现类
     * @param entity     导出数据查询条件
     * @param clazz      导出数据实体类
     * @param fileIndex  文件下标
     * @param sheetCount 工作簿数量
     * @param <T>        导出数据实体类泛型
     */
    private static <T> void generateExcelFile(String pathName,
                                              String sheetName,
                                              IBaseService<T> service,
                                              T entity,
                                              Class<T> clazz,
                                              int fileIndex,
                                              int sheetCount) {
        try (ExcelWriter excelWriter = EasyExcel.write(pathName).build()) {
            IntStream.range(0, sheetCount).forEach(i -> {
                WriteSheet ws = EasyExcel.writerSheet(i, sheetName + (i + 1))
                        .head(clazz)
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .build();
                int current = (fileIndex * Constants.PER_FILE_SHEET_COUNT) + i + 1;
                List<T> list = service.getPageList(entity, new Page<>(current, Constants.PER_WRITE_ROW_COUNT));
                excelWriter.write(list, ws);
            });
        }
    }

    /**
     * 处理异步任务异常信息
     *
     * @param finalFileName 文件路径
     * @param sheetName     工作簿名称
     * @param e             异常信息
     * @return 路径
     */
    private static Path processException(String finalFileName, String sheetName, Throwable e) {
        log.error("导出【{}】-【{}】错误：{}", finalFileName, sheetName, e.toString());
        return null;
    }

    /**
     * 处理异步任务
     *
     * @param future 异步任务
     * @return 路径
     */
    private static Path handleFuture(CompletableFuture<Path> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("异步任务【{}】错误：{}", future, e.toString());
            return null;
        }
    }

    /**
     * 写入响应内容
     *
     * @param response 响应对象
     * @param fileName 文件名
     * @param is       输入流
     */
    private static void writeResponse(HttpServletResponse response, String fileName, InputStream is) {
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName);

        try (OutputStream outputStream = response.getOutputStream()) {
            IoUtil.copy(is, outputStream, IoUtil.DEFAULT_BUFFER_SIZE);
        } catch (IOException e) {
            log.error("下载失败：{}", e.toString());
        }
    }
}
