# AOP_Javassist

## Javassist

> Javassist 是一个执行字节码操作的库。它可以在一个已经编译好的类中添加新的方法，或者是修改已有的方法，并且不需要对字节码方面有深入的了解。

## 工作时机

> Javassist 可以绕过编译，直接操作字节码，从而实现代码注入，所以使用 Javassist 的时机就是在构建工具 Gradle 将源文件编译成 .class 文件之后，在将 .class 打包成 dex 文件之前。

## Gradle插件(Plugin)

1. 新建一个`Module`， 因没有`gradle plugin`给你选，可随便选一个`Module`类型（如`Phone&Tablet Module`或`Android Library`）；

2. 将`Module`里面的内容删除，只保留`build.gradle`文件和`src/main`目录；

3. 自定义`Plugin`类，实现`org.gradle.api.Plugin`接口；

4. 告诉`gradle`哪个是自定义插件类；在`main`目录下依次新建`resources/META-INF/gradle-plugins`目录，最后在gradle-plugins目录里新建properties文件，注意这个文件命名，eg:com.aliya.plugin.properties，而在其他项目build.gradle使用该插件 `apply plugin: 'com.aliya.plugin'`；

5. 然后在`com.aliya.plugin.properties`文件里指明自定义插件类,`implementation-class=com.aliya.plugin.自定义Plugin`；

6. `build.gradle`配置

```
apply plugin: 'groovy'
apply plugin: 'maven'

dependencies {
    compile gradleApi()     // gradle sdk
    compile localGroovy()   // groovy sdk

    compile 'com.android.tools.build:gradle:3.1.2'
    compile 'org.javassist:javassist:3.20.0-GA'
}

// 发布到本地
uploadArchives {
    repositories {
        mavenDeployer {
            repository(url: uri('../repo'))     // 仓库的路径，此处是项目根目录下的 repo 的文件夹
            pom.groupId = 'com.aliya.plugin'    // groupId ，自行定义，一般是包名
            pom.artifactId = 'plugin-test'      // artifactId ，自行定义
            pom.version = '1.0.0'               // version 版本号
        }
    }
}
```

执行`uploadArchives Task`即发布到本地仓库

7. 项目根目录`build.gradle`

```
    repositories {

        // 本地依赖
        maven {
            url("repo")
        }
    }

    dependencies {
            classpath 'com.android.tools.build:gradle:3.1.2'
            classpath 'com.aliya.plugin:plugin-test:1.0.0'
        }
```

### 开发只针对当前项目的Gradle插件

只是针对当前项目开发的Gradle插件相对较简单。无需打包发布这个过程，步骤之前所提到的很类似，只是有几点需要注意：

> 1. 新建的Module名称必须为BuildSrc;
> 2. 无需resources目录;

其中，`build.gradle`内容为：
```
apply plugin: 'groovy'

dependencies {
    compile gradleApi()//gradle sdk
    compile localGroovy()//groovy sdk
}

```

直接在app的`build.gradle`下加入

```
apply plugin: com.aliya.plugin.自定义Plugin
```

`clean`一下，再`make project`。