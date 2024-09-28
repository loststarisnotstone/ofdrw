import jpype

# 启动 JVM，指定 JAR 文件路径
jpype.startJVM(jpype.getDefaultJVMPath(), "-Djava.class.path=C:/Users/liugu/Documents/GitHub/ofdrw/ofdrw.jar")

# 导入 OFDDocument 类
OFDDocument = jpype.JClass("org.ofdrw.core.OFDDocument")

# 使用 OFDDocument 类进行操作
document = OFDDocument("example.ofd")
