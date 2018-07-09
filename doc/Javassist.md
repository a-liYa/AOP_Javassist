Javassist 使用指南

## 1 读写字节码

类 Javaassit.CtClass 表示 class 文件，一个 GtClass (编译时类）对象可以处理一个 class 文件；

```
ClassPool pool = ClassPool.getDefault();
CtClass cc = pool.get("com.aliya.Test");
```

从实现的角度来看，ClassPool 是一个存储 CtClass 的 Hash 表，类的名称作为 Hash 表的 key。ClassPool 的 get() 函数用于从 Hash 表中查找 key 对应的 CtClass 对象。如果没有找到，get() 函数会创建并返回一个新的 CtClass 对象，这个新对象会保存在 Hash 表中。


```
// 将 CtClass 对象转换成类文件并写到本地磁盘
cc.writeFile();


// 获取修改过的字节码
byte[] b = cc.toBytecode();


// 直接将 CtClass 转换成 Class 对象
Class clazz = cc.toClass();

```

### 1.1 定义新类

```
// 定义了一个空的 Point 类
CtClass point = pool.makeClass("com.aliya.Point");

// 添加方法 public abstract Void test();
point.addMethod(CtNewMethod.abstractMethod(pool.get("java.lang.Void"), "test", null, null, point));

// 创建新接口
CtClass ipoint = pool.makeInterface();

```

### 1.2 将类冻结

> 如果一个 CtClass 对象通过 writeFile(), toClass(), toBytecode() 被转换成一个类文件，此 CtClass 对象会被冻结起来，不允许再修改。

但是，一个冷冻的 CtClass 也可以被解冻，例如：
```
CtClasss cc = ...;
    :
cc.writeFile();
cc.defrost();               // 解冻
cc.setSuperclass(...);      // 因为类已经被解冻，所以这里可以调用成功
```

如果 `ClassPool.doPruning = true`，Javassist 在冻结 CtClass 时，会修剪 CtClass 的数据结构。为了减少内存的消耗，修剪操作会丢弃 CtClass 对象中不必要的属性。一个 CtClass 对象被修改之后，方法的字节码是不可访问的，但是方法名称、方法签名、注解信息可以被访问。修剪过的 CtClass 对象不能再次被解冻。

```
cc.stopPruning(); // 可以用来驳回修剪操作。
 :
cc.writeFile(); // CtClass 没有被修剪，所以在 writeFile() 之后，可以被解冻。

cc.debugWriteFile(); // 临时需要停止修剪和冻结，然后保存一个修改过的类文件到磁盘。

```

### 1.3 类搜索路径

> 通过 ClassPool.getDefault() 获取的 ClassPool 使用 JVM 的类搜索路径。如果程序运行在 JBoss 或者 Tomcat 等 Web 服务器上，ClassPool 可能无法找到用户的类，因为 Web 服务器使用多个类加载器作为系统类加载器。在这种情况下，ClassPool 必须添加额外的类搜索路径。

```
// 将 this 指向的类添加到 pool 的类加载路径中
pool.insertClassPath(new ClassClassPath(this.getClass()));

// 注册一个目录作为类搜索路径
pool.insertClassPath("/usr/local/javalib");

// 注册一个URL作为类搜索路径
ClassPath cp = new URLClassPath("www.javassist.org", 80, "/java/", "org.javassist.");
pool.insertClassPath(cp);

// 添加 byte 数组给 ClassPool 来构造一个 CtClass 对象
pool.insertClassPath(new ByteArrayClassPath(className, byteArray));

```

## 2 ClassPool

> ClassPool 是 CtClass 对象的容器。因为编译器在编译引用 CtClass 代表的 Java 类的源代码时，可能会引用 CtClass 对象，所以一旦一个 CtClass 被创建，它就被保存在 ClassPool 中.

### 2.1 避免内存溢出

1.调用 CtClass.detach() 方法，将 CtClass 对象将被从 ClassPool 中删除。
```
CtClass cc = ... ;
cc.detach();
```

2.用新的 ClassPool 替换旧的 ClassPool，旧的 ClassPool 被垃圾回收掉时，包含在 ClassPool 中的 CtClass 对象也会被回收。
```
ClassPool pool = new ClassPool(true);
```

### 2.2 级联的 ClassPools
> 如果程序正在 Web 应用程序服务器上运行，则可能需要创建多个 ClassPool 实例; 应为每一个 ClassLoader 创建一个 ClassPool 的实例。多个 ClassPool 对象可以像 java.lang.ClassLoader 一样级联。

```
ClassPool parent = ClassPool.getDefault();
ClassPool child = new ClassPool(parent);
child.insertClassPath("./classes");
```
如果调用 child.get()，子 ClassPool 首先委托给父 ClassPool。如果父 ClassPool 找不到类文件，那么子 ClassPool 会尝试在 ./classes 目录下查找类文件

如果设置 child.childFirstLookup = true，那么子类 ClassPool 会在委托给父 ClassPool 之前尝试查找类文件。

### 2.3 拷贝一个已经存在的类来定义一个新的类
```
ClassPool pool = ClassPool.getDefault();
CtClass cc = pool.get("Point");
cc.setName("Pair");
```

注意下面对比：
```
ClassPool pool = ClassPool.getDefault();
CtClass cc = pool.get("Point");
CtClass cc1 = pool.get("Point");    // cc1 与 cc 指向同一实例.
cc.setName("Pair");
CtClass cc2 = pool.get("Pair");     // cc2 cc1 cc 指向同一实例.
CtClass cc3 = pool.get("Point");    // cc3 与 cc 不相同.
```

### 2.4 通过重命名冻结的类来生成新的类

```
ClassPool pool = ClassPool.getDefault();
CtClass cc = pool.get("Point");
cc.writeFile();
CtClass cc2 = pool.getAndRename("Point", "Pair");
```

## 3 类加载器 (Class Loader)

如果事先知道要修改哪些类，修改类的最简单方法如下：

1. 调用 ClassPool.get() 获取 CtClass 对象，
2. 修改 CtClass
3. 调用 CtClass 对象的 writeFile() 或者 toBytecode() 获得修改过的类文件。

### 3.1 CtClass.toClass()

> CtClass 的 toClass() 方法请求当前线程的上下文类加载器，加载 CtClass 对象所表示的类。

注意：`如果 JVM 在 toClass() 调用之前加载了原始的目标类，后续加载修改的目标类将会失败（LinkageError 抛出）。`

CtClass#toClass(classLoader); 可以传递参数为ClassLoader;

### javassist.Loader
Javassit 提供一个类加载器 javassist.Loader。它使用 javassist.ClassPool 对象来读取类文件。

```
Loader loader = new Loader(pool);
loader.addTranslator(); // 添加事件监听器。当类加载器加载类时会通知监听器。
```