#-*- coding: utf-8 -*-

from numpy import *

# 引入Tkinter GUI包
from Tkinter import *

# 引入回归树算法实现
import regTrees

# 引入 matplotlib 包
import matplotlib
# 设定 matplotlib 后端为 TkAgg
matplotlib.use('TkAgg')
# 引入相关包，将 TkAgg 与 matplotlib图链接起来
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from matplotlib.figure import Figure

# 绘图函数，根据输入的参数绘制图形
def reDraw(tolS,tolN):
    # 调用 Figure 对象的 clf() 方法清理画布
    reDraw.f.clf()
    
    # 添加 Axes 对象
    reDraw.a = reDraw.f.add_subplot(111)
    
    # 检查复选框是否被选中
    if chkBtnVar.get():
        # 绘制模型树
        # tolN至少为2
        if tolN < 2: tolN = 2
        
        # 创建模型树
        myTree=regTrees.createTree(reDraw.rawDat, regTrees.modelLeaf,\
                                   regTrees.modelErr, (tolS,tolN))
        # 计算预测值向量
        yHat = regTrees.createForeCast(myTree, reDraw.testDat, \
                                       regTrees.modelTreeEval)
    else:
        # 创建回归树
        myTree=regTrees.createTree(reDraw.rawDat, ops=(tolS,tolN))
        
        # 计算预测值向量
        yHat = regTrees.createForeCast(myTree, reDraw.testDat)
    
    # 调用 scatter() 方法绘制真实数据图
    reDraw.a.scatter(reDraw.rawDat[:,0], reDraw.rawDat[:,1], s=5)
    
    # 预测值采用 plot() 方法绘制
    reDraw.a.plot(reDraw.testDat, yHat, linewidth=2.0)
    
    # 在画布上显示绘制的图形
    reDraw.canvas.show()

# 读取界面输入
# 输入值为tolS和tolN 
def getInputs():

    # 如果输入错误则使用默认值替代（tolS=1.0，tolN=10）
    try: tolN = int(tolNentry.get())
    except: 
        tolN = 10 
        print "enter Integer for tolN"
        tolNentry.delete(0, END)
        tolNentry.insert(0,'10')

    # 如果输入错误则使用默认值替代
    try: tolS = float(tolSentry.get())
    except: 
        tolS = 1.0 
        print "enter Float for tolS"
        tolSentry.delete(0, END)
        tolSentry.insert(0,'1.0')
    return tolN,tolS

# 绘图函数
def drawNewTree():
    # 从界面读取输入
    tolN,tolS = getInputs()
    # 根据输入绘制图形
    reDraw(tolS,tolN)

# 初始化Tk窗口
root=Tk()

# 关联 matplotlib 图形到 Tk 组件
reDraw.f = Figure(figsize=(5,4), dpi=100)

# 创建 Canvas 画布
reDraw.canvas = FigureCanvasTkAgg(reDraw.f, master=root)

# 显示画布
reDraw.canvas.show()
reDraw.canvas.get_tk_widget().grid(row=0, columnspan=3)

# 添加输入框
# 添加输入框标签并添加到 Tk 窗口上，注意位置采用 Tk 的 Grid 方式
Label(root, text="tolN").grid(row=1, column=0)
tolNentry = Entry(root)
tolNentry.grid(row=1, column=1)
tolNentry.insert(0,'10')
Label(root, text="tolS").grid(row=2, column=0)
tolSentry = Entry(root)
tolSentry.grid(row=2, column=1)
tolSentry.insert(0,'1.0')

# 添加画图触发按钮，点击后调用 drawNewTree() 函数
Button(root, text="ReDraw", command=drawNewTree).grid(row=1, column=2, rowspan=3)

# 添加复选框，选中后绘制模型树，默认绘制回归树
chkBtnVar = IntVar()
chkBtn = Checkbutton(root, text="Model Tree", variable = chkBtnVar)
chkBtn.grid(row=3, column=0, columnspan=2)

# 读取数据文件，并生成测试数据
reDraw.rawDat = mat(regTrees.loadDataSet('sine.txt'))
reDraw.testDat = arange(min(reDraw.rawDat[:,0]),max(reDraw.rawDat[:,0]),0.01)

# 调用画图函数，采用默认值
reDraw(1.0, 10)

# Tk GUI 主循环
root.mainloop()

