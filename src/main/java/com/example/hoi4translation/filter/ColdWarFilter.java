package com.example.hoi4translation.filter;

import cn.hutool.core.io.file.FileNameUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ColdWarFilter implements FileFilter {
    private static final List<String> files = new ArrayList<>();

    static {
        String[] filePaths = {
                "common/characters/",
                "common/decisions/",
                "common/ideas/",
                "common/intelligence_agencies/",
                "common/names/",
                "common/national_focus/",
                "common/on_actions/",
                "common/operations/",
                "common/scripted_effects/",
                "common/scripted_triggers/",
                "common/scripted_guis/",
                "common/scripted_localisation",
                "common/units/codenames_operatives/",
                "common/units/names/",
                "common/units/names_division",
                "common/units/names_divisions/",
                "common/units/names_railway_guns/",
                "common/units/names_ships/",
                "events/",
                "history/countries/",
                "history/units/"
        };

        files.addAll(Arrays.asList(filePaths));
    }

    @Override
    public boolean accept(File pathname) {
        String path = pathname.getPath().replace(File.separator, "/");
        return files.stream().anyMatch(file -> path.contains(file) && FileNameUtil.isType(path, "txt", "yml"));
    }
}
