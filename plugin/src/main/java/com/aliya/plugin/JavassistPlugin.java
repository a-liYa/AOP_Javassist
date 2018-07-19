package com.aliya.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.build.gradle.internal.api.ApplicationVariantImpl;
import com.android.build.gradle.internal.variant.ApkVariantData;
import com.android.build.gradle.tasks.GenerateBuildConfig;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;

class JavassistPlugin implements Plugin<Project> {

    public void apply(Project project) {
        Logger logger = project.getLogger();
        logger.error("========================");
        logger.error("Javassist开始修改Class!");
        logger.error("========================");

        // has plugin: 'com.android.application'
        if (project.getPlugins().hasPlugin(AppPlugin.class)) {
            logger.error("ApplicationPlugin");

//            project.getDependencies().add("implementation", "io.github.prototypez:save-state-core:0.1");
//            project.getDependencies().add("annotationProcessor", "io.github.prototypez:save-state-processor:0.1.4");

        }

        // has plugin: 'com.android.library'
        if (project.getPlugins().hasPlugin(LibraryPlugin.class)) {
            logger.error("LibraryPlugin");
        }

        AppExtension android = project.getExtensions().getByType(AppExtension.class);
        android.registerTransform(new JavassistTransform(project));

        DomainObjectSet<ApplicationVariant> variants = android.getApplicationVariants();
        variants.all(variant -> {
            if (variant instanceof ApplicationVariantImpl) {
                ApplicationVariantImpl variantImpl = (ApplicationVariantImpl) variant;
                ApkVariantData variantData = variantImpl.getVariantData();

                String taskName = variantData.getTaskName("appTest", "Suffix");

                Task task = project.task(taskName);

                task.doFirst(innerTask -> {
                    logger.error("inner doFirst");
                });
                task.doLast(innerTask -> {
                    logger.error("inner doLast");
                });

                GenerateBuildConfig buildConfigTask = variantImpl.getGenerateBuildConfig();
                if (buildConfigTask != null) {
                    task.dependsOn(buildConfigTask);
                    buildConfigTask.finalizedBy(task);
                }
            }

        });

    }
}