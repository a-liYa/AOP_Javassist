package com.aliya.plugin.compiling;

import com.android.annotations.NonNull;
import com.android.builder.internal.ClassFieldImpl;
import com.android.builder.model.ClassField;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Closer;
import com.squareup.javawriter.JavaWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * AppConfig.class 构建器
 *
 * @author a_liYa
 * @date 2018/7/18 11:31.
 */
public class AppConfigGenerator {

    public static final String APP_CONFIG_NAME = "AppConfig";

    private static final Set<Modifier> PUBLIC_FINAL = EnumSet.of(Modifier.PUBLIC, Modifier.FINAL);
    private static final Set<Modifier> PUBLIC_STATIC_FINAL =
            EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

    private final File mGenFolder;
    private final String mPackageName;

    private final List<ClassField> mFields = Lists.newArrayList();

    public AppConfigGenerator(File genFolder, String packageName) {
        mGenFolder = genFolder;
        mPackageName = packageName;
    }

    public AppConfigGenerator addField(
            @NonNull String type, @NonNull String name, @NonNull String value) {
        mFields.add(new ClassFieldImpl(type, name, value));
        return this;
    }

    /**
     * Generates the AppConfig class.
     */
    public void generate() throws IOException {
        File pkgFolder = getFolderPath();
        if (!pkgFolder.isDirectory()) {
            if (!pkgFolder.mkdirs()) {
                throw new RuntimeException("Failed to create " + pkgFolder.getAbsolutePath());
            }
        }

        File appConfigJava = new File(pkgFolder, APP_CONFIG_NAME + ".java");

        Closer closer = Closer.create();
        try {
            FileOutputStream fos = closer.register(new FileOutputStream(appConfigJava));
            OutputStreamWriter out = closer.register(new OutputStreamWriter(fos, Charsets.UTF_8));
            JavaWriter writer = closer.register(new JavaWriter(out));

            writer.emitJavadoc("自动生成的文件，不需要修改")
                    .emitPackage(mPackageName)
                    .beginType(APP_CONFIG_NAME, "class", PUBLIC_FINAL);

            for (ClassField field : mFields) {
                emitClassField(writer, field);
            }

            writer.endType();
        } catch (Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }



    }

    private File getFolderPath() {
        return new File(mGenFolder, mPackageName.replace('.', File.separatorChar));
    }

    private static void emitClassField(JavaWriter writer, ClassField field) throws IOException {
        String documentation = field.getDocumentation();
        if (!documentation.isEmpty()) {
            writer.emitJavadoc(documentation);
        }
        for (String annotation : field.getAnnotations()) {
            writer.emitAnnotation(annotation);
        }
        writer.emitField(
                field.getType(),
                field.getName(),
                PUBLIC_STATIC_FINAL,
                field.getValue());
    }

}
