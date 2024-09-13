package com.example.hoi4translation.strategy.service.impl.paratranz;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.FileVO;
import com.example.hoi4translation.domain.vo.PageVO;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.strategy.service.ParatranzFileProcessorStrategy;

import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class ParatranzGeneralFileProcessorStrategy implements ParatranzFileProcessorStrategy {

    public ParatranzGeneralFileProcessorStrategy() {
    }

    @Override
    public void processFiles(String authorization, List<FileVO> fileList, List<StringVO> list, Set<Word> words) {
        List<StringVO> voList = fileList.stream()
                .map(file -> parseFile(authorization, file))
                .flatMap(List::stream)
                .toList();
        list.addAll(voList);
    }

    private List<StringVO> parseFile(String authorization, FileVO file) {
        int pageSize = 800;
        int pageCount = (file.getTotal() - 1) / pageSize + 1;

        return IntStream.rangeClosed(1, pageCount)
                .mapToObj(pageNum -> {
                    String str = "https://paratranz.cn/api/projects/{}/strings?file={}&stage=1&stage=2&stage=3&stage=5&stage=9&page={}&pageSize=800";
                    String url = StrUtil.format(str, file.getProject(), file.getId(), pageNum);
                    int retryCount = 0;
                    int maxRetries = 5;
                    long retryDelay = 2000;
                    while (retryCount < maxRetries) {
                        try (HttpResponse response = HttpRequest.get(url)
                                .auth(authorization)
                                .execute()) {
                            if (response.getStatus() == 200) {
                                return response.body();
                            } else if (response.getStatus() == 429) {
                                Thread.sleep(retryDelay);
                                retryCount++;
                            } else {
                                throw new RuntimeException("HTTP error: " + response.getStatus());
                            }
                        } catch (Exception e) {
                            if (retryCount >= maxRetries - 1) {
                                throw new RuntimeException("Max retries reached, aborting.");
                            }
                            retryCount++;
                            try {
                                Thread.sleep(retryDelay);
                            } catch (InterruptedException interruptedException) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                    return null;
                })
                .map(body -> {
                    if (body == null) {
                        return Collections.<StringVO>emptyList();
                    }
                    try {
                        PageVO pageVO = JSONUtil.toBean(body, PageVO.class);
                        if (pageVO == null || pageVO.getResults() == null) {
                            return Collections.<StringVO>emptyList();
                        }
                        return pageVO.getResults();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(Collection::stream)
                .filter(result -> StrUtil.isNotBlank(result.getTranslation()))
                .toList();
    }
}
