#-*- coding: utf-8 -*-

'''
Created on Jan 8, 2011

@author: Peter
'''

from numpy import *

# 读取数据到矩阵
def loadDataSet(fileName):
    dataMat = []
    
    # 打开文件
    fr = open(fileName)
    
    # 读取文件的每一行
    for line in fr.readlines():
        # 将每一行的内容根据制表符进行分割
        curLine = line.strip().split('\t')
        
        # 将每个数据转成浮点型
        fltLine = map(float,curLine)
        dataMat.append(fltLine)
    return dataMat
    
    
# 线性回归算法实现，返回构建的模型
def linearSolve(dataSet):
    m,n = shape(dataSet)
    
    # 创建X与Y矩阵
    X = mat(ones((m,n))); Y = mat(ones((m,1)))
    X[:,1:n] = dataSet[:,0:n-1]; Y = dataSet[:,-1]
    xTx = X.T*X
    
    # 判断是否为奇异矩阵
    if linalg.det(xTx) == 0.0:
        raise NameError('This matrix is singular, cannot do inverse,\n\
        try increasing the second value of ops')
    ws = xTx.I * (X.T * Y)
    return ws,X,Y
    
# 切分数据集
def binSplitDataSet(dataSet, feature, value):
    # 根据特征和特征值，通过过滤方式对数据集进行切分
    mat0 = dataSet[nonzero(dataSet[:,feature] > value)[0],:]
    mat1 = dataSet[nonzero(dataSet[:,feature] <= value)[0],:]
    
    # 返回切分后的两个数据矩阵
    return mat0,mat1

# 回归树叶节点生成函数
def regLeaf(dataSet):
    # 返回目标变量的均值
    return mean(dataSet[:,-1])

# 回归树误差估计函数
def regErr(dataSet):
    # 计算目标变量的平方误差 var() 是均方差函数
    return var(dataSet[:,-1]) * shape(dataSet)[0]

# 模型树叶节点生成函数
def modelLeaf(dataSet):
    # 根据数据集创建线性回归模型
    ws,X,Y = linearSolve(dataSet)
    return ws

# 模型树误差估计函数
def modelErr(dataSet):
    # 根据数据集创建线性回归模型
    ws,X,Y = linearSolve(dataSet)
    
    # 返回yHat与Y之间的平方误差
    yHat = X * ws
    return sum(power(Y - yHat,2))

# 选择最佳切分方案：构建回归树或模型树的核心函数
# 参数为：数据矩阵，建立叶节点的函数，误差计算函数，构建树所需的其他参数元组
def chooseBestSplit(dataSet, leafType=regLeaf, errType=regErr, ops=(1,4)):
    # 读取ops里的参数，tolS为容许的误差下降值，tolN为切分的最小样本数
    tolS = ops[0]; tolN = ops[1]
    
    # 如果目标值都相等则返回
    if len(set(dataSet[:,-1].T.tolist()[0])) == 1:
        return None, leafType(dataSet)

    # 初始化循环变量
    m,n = shape(dataSet)
    S = errType(dataSet)
    bestS = inf; bestIndex = 0; bestValue = 0
    
    # 遍历每个特征
    for featIndex in range(n-1):
        # 遍历该特征的每个特征值
        for splitVal in set(dataSet[:,featIndex].flat):
            # 尝试切分数据集
            mat0, mat1 = binSplitDataSet(dataSet, featIndex, splitVal)
            
            # 判断切分后的数据集大小，如果太小则继续下一个切分
            if (shape(mat0)[0] < tolN) or (shape(mat1)[0] < tolN): continue
            
            # 计算切分的误差
            newS = errType(mat0) + errType(mat1)
            
            # 判断误差是否更小
            if newS < bestS: 
                bestIndex = featIndex
                bestValue = splitVal
                bestS = newS
                
    # 判断误差是否减少足够大，如果减少不大则返回
    if (S - bestS) < tolS: 
        return None, leafType(dataSet)
    
    # 根据选取的特征和特征值切分数据
    mat0, mat1 = binSplitDataSet(dataSet, bestIndex, bestValue)
    
    # 判断切分后的数据集大小，如果数据集太小则退出
    if (shape(mat0)[0] < tolN) or (shape(mat1)[0] < tolN):
        return None, leafType(dataSet)
    
    # 返回最优切分特征及特征值
    return bestIndex,bestValue

# 创建树的算法实现
# 参数为：数据矩阵，建立叶节点的函数，误差计算函数，构建树所需的其他参数元组
def createTree(dataSet, leafType=regLeaf, errType=regErr, ops=(1,4)):

    # 尝试切分数据为两部分
    # 选择最优的切分方案
    feat, val = chooseBestSplit(dataSet, leafType, errType, ops)
    
    # 如果满足了停止继续切分的条件则返回常数（回归树）或线性方程（模型树）
    if feat == None: return val
    
    # 将数据集分成两部分
    retTree = {}
    retTree['spInd'] = feat
    retTree['spVal'] = val
    lSet, rSet = binSplitDataSet(dataSet, feat, val)
    
    # 继续递归左子树和右子树
    retTree['left'] = createTree(lSet, leafType, errType, ops)
    retTree['right'] = createTree(rSet, leafType, errType, ops)
    return retTree

# 回归树叶节点预测，两个参数是为了与modelTreeEval保持一致
def regTreeEval(model, inDat):
    return float(model)

# 模型树叶节点预测
def modelTreeEval(model, inDat):
    # 对数据进行格式化处理
    # 在原数据矩阵上增加第0列并返回预测值
    n = shape(inDat)[1]
    X = mat(ones((1,n+1)))
    X[:,1:n+1]=inDat
    return float(X*model)

# 判断是否是叶节点
def isTree(obj):
    return (type(obj).__name__=='dict')

# 自顶向下遍历树
# 递归实现
# 参数是树，输入数据（数据点或行向量），叶节点预测函数
def treeForeCast(tree, inData, modelEval=regTreeEval):
    # 如果是叶节点则直接调用预测函数返回
    if not isTree(tree): return modelEval(tree, inData)
    
    # 自顶向下遍历树，进入左子树或右子树递归调用
    if inData[tree['spInd']] > tree['spVal']:
        # 判断左子树是否已经是叶子节点
        if isTree(tree['left']): return treeForeCast(tree['left'], inData, modelEval)
        else: return modelEval(tree['left'], inData)
    else:
        # 判断右子树是否已经是叶子节点
        if isTree(tree['right']): return treeForeCast(tree['right'], inData, modelEval)
        else: return modelEval(tree['right'], inData)

# 预测值计算函数
# 参数是树，输入数据，叶节点预测函数
def createForeCast(tree, testData, modelEval=regTreeEval):
    m=len(testData)
    yHat = mat(zeros((m,1)))
    
    # 遍历测试数据中的数据点或行向量
    for i in range(m):
        # 调用treeForeCast()函数生成预测值
        yHat[i,0] = treeForeCast(tree, mat(testData[i]), modelEval)
    return yHat

"""
# 读取训练和测试数据
trainFile = '/home/shiyanlou/mylab11/bikeSpeedVsIq_train.txt'
testFile = '/home/shiyanlou/mylab11/bikeSpeedVsIq_test.txt'

trainMat = mat(loadDataSet(trainFile))
testMat = mat(loadDataSet(testFile))

# 创建回归树并计算R^2值
myTree = createTree(trainMat, ops=(1,20))
yHat = createForeCast(myTree, testMat[:,0])
regTreeR2 = corrcoef(yHat, testMat[:,1], rowvar=0)[0,1]

# 创建模型树并计算R^2值
myTree = createTree(trainMat, modelLeaf, modelErr, ops=(1,20))
yHat = createForeCast(myTree, testMat[:,0], modelTreeEval)
modelTreeR2 = corrcoef(yHat, testMat[:,1], rowvar=0)[0,1]

# 创建线性模型并计算R^2
ws, X, Y = linearSolve(trainMat)
for i in range(shape(testMat)[0]):
    yHat[i] = testMat[i, 0] * ws[1,0] + ws[0,0]
linearTreeR2 = corrcoef(yHat, testMat[:,1], rowvar=0)[0,1]

# 输出三种模型的R^2值，越接近1表示效果越好
print "regTreeR^2=",regTreeR2
print "modelTreeR^2=",modelTreeR2
print "linearTreeR^2=",linearTreeR2
"""    
    