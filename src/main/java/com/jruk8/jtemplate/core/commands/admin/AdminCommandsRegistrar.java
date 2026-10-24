package com.jruk8.jtemplate.core.commands.admin;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.admin.reload.ReloadCommand;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.paper.util.sender.Source;

public class AdminCommandsRegistrar implements Bootstrap {

    private final AnnotationParser<Source> annotationParser;
    private final Reloader reloader;
    private final CommandContext ctx;

    public AdminCommandsRegistrar(AnnotationParser<Source> annotationParser, CommandContext ctx, Reloader reloader) {
        this.annotationParser = annotationParser;
        this.reloader = reloader;
        this.ctx = ctx;
    }

    @Override
    public void register() {
        annotationParser.parse(new ReloadCommand(reloader, ctx));
    }
}
