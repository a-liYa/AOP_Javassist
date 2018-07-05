Javassist 使用指南

## 读写字节码

类 Javaassit.CtClass 表示 class 文件，一个 GtClass (编译时类）对象可以处理一个 class 文件；

```
ClassPool pool = ClassPool.getDefault();
CtClass cc = pool.get("com.aliya.Test");
```

从实现的角度来看，ClassPool 是一个存储 CtClass 的 Hash 表，类的名称作为 Hash 表的 key。ClassPool 的 get() 函数用于从 Hash 表中查找 key 对应的 CtClass 对象。如果没有找到，get() 函数会创建并返回一个新的 CtClass 对象，这个新对象会保存在 Hash 表中。
