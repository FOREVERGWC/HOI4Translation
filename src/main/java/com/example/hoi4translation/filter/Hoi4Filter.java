package com.example.hoi4translation.filter;

import cn.hutool.core.io.file.FileNameUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

@Component
public class Hoi4Filter implements FileFilter {
    private static final Set<String> files = new HashSet<>();

    static {
        String[] filePaths = {
                "common/characters/",
                "common/decisions/",
                "common/ideas/",
                "common/intelligence_agencies/",
                "common/military_industrial_organization/organizations/",
                "common/names/",
                "common/national_focus/",
                "common/on_actions/",
                "common/operations/",
                "common/scripted_effects/",
                "common/scripted_triggers/",
                "common/units/codenames_operatives/",
                "common/units/names/",
                "common/units/names_divisions/",
                "common/units/names_railway_guns/",
                "common/units/names_ships/",
                "events/",
                "history/countries/",
                "history/units/"
        };
        Collections.addAll(files, filePaths);
    }

    @Override
    public boolean accept(File pathname) {
        String path = pathname.getPath().replace(File.separator, "/");
        return files.stream().anyMatch(file -> path.contains(file) && FileNameUtil.isType(path, "txt", "yml"));
    }
}
