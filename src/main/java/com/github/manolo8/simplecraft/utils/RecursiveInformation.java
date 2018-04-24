package com.github.manolo8.simplecraft.utils;

import com.github.manolo8.simplecraft.modules.group.Group;

public class RecursiveInformation {

    public static StringBuilder buildPermissionsDetails(Group group, StringBuilder builder) {
        builder.append("§aGrupo: ").append(group.getName()).append("\n");
        for (String string : group.getPermissions()) {
            builder.append("§b-> ").append(string).append("\n");
        }

        if (group.getParent() != null) {
            builder.append("§aParentes: ");
            return buildPermissionsDetails(group.getParent(), builder);
        }

        return builder;
    }
}
