package me.couph.grizzlytools.util;

import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlaceholderProperties {
    private Map<String, PlaceHolderResult> varibleResultMap = Maps.newHashMap();

    public void put(String variable, PlaceHolderResult result) {
        this.varibleResultMap.put(variable, result);
    }

    public String format(String string, GrizzlyPickaxe grizzlyPickaxe) {
        String original = string;
        List<String> variablesToReplace = (List<String>)this.varibleResultMap.keySet().stream().filter(original::contains).collect(Collectors.toList());
        for (String variable : variablesToReplace)
            original = original.replace(variable, ((PlaceHolderResult)this.varibleResultMap.get(variable)).getResult(grizzlyPickaxe));
        return original;
    }
}
