#-*- coding: utf-8 -*-

'''
Created on Nov 28, 2010
Adaboost is short for Adaptive Boosting
@author: Peter
'''
from numpy import *

# 读取训练数据到数据矩阵和类别标签列表
def loadDataSet(filename):
    dataMat = []; labelMat = []
    
    # 打开文件
    fr = open(filename)
    
    # 遍历数据文件每一行
    for line in fr.readlines():
        
        # 根据制表符切分每一行的数据
        currLine = line.strip().split('\t')
        lineArr =[]
        
        # 将22项数据都去进lineArr列表
        for i in range(21):
            lineArr.append(float(currLine[i]))
        
        # 添加到矩阵和类别标签列表
        dataMat.append(lineArr)
        
        # 第22列为类别
        labelMat.append(float(currLine[21]))
    return dataMat, labelMat
    
# 分类函数，通过输入的阈值参数进行分类
def stumpClassify(dataMatrix,dimen,threshVal,threshIneq):

    # 初始化分类结果为1
    retArray = ones((shape(dataMatrix)[0],1))
    
    # 判断阈值中条件
    if threshIneq == 'lt':
        # 条件为小于等于阈值的类别为-1
        retArray[dataMatrix[:,dimen] <= threshVal] = -1.0
    else:
        retArray[dataMatrix[:,dimen] > threshVal] = -1.0
    return retArray

# 单层决策树生成函数，找到并建立数据集上的最佳的单层决策树
def buildStump(dataArr,classLabels,D):
    # 构建输入数据矩阵和类别标签矩阵
    dataMatrix = mat(dataArr); labelMat = mat(classLabels).T
    m,n = shape(dataMatrix)
    
    # 创建bestStump用于存储最佳单层决策树的相关信息
    numSteps = 10.0; bestStump = {}; bestClasEst = mat(zeros((m,1)))
    
    # 初始化错误率为无穷大
    minError = inf
    
    # 循环遍历数据集的所有特征
    for i in range(n):
        # 通过计算数据最大最小值来获取步长
        rangeMin = dataMatrix[:,i].min(); rangeMax = dataMatrix[:,i].max();
        stepSize = (rangeMax-rangeMin)/numSteps
        
        # 在当前特征对应的数据上进行遍历
        for j in range(-1,int(numSteps)+1):
        
            # 再大于和小于之间切换不等式条件
            for inequal in ['lt', 'gt']:

                # 得到本次执行的阈值
                threshVal = (rangeMin + float(j) * stepSize)
                
                # 获得数据分类结果
                predictedVals = stumpClassify(dataMatrix,i,threshVal,inequal)
                
                # 计算错误率
                errArr = mat(ones((m,1)))
                errArr[predictedVals == labelMat] = 0
                weightedError = D.T*errArr
                
                # 判断加权错误率是否小于当前的最小错误率
                if weightedError < minError:
                    # 更新决策树
                    minError = weightedError
                    bestClasEst = predictedVals.copy()
                    bestStump['dim'] = i
                    bestStump['thresh'] = threshVal
                    bestStump['ineq'] = inequal
    # 返回最佳单层决策树
    return bestStump,minError,bestClasEst

# AdaBoost训练过程，返回训练结果为弱分类器
def adaBoostTrainDS(dataArr,classLabels,numIt=40):
    
    # 初始化要返回的弱分类器列表
    weakClassArr = []
    m = shape(dataArr)[0]
    
    # 初始化D值，赋予相等的权重
    D = mat(ones((m,1))/m)
    aggClassEst = mat(zeros((m,1)))
    
    # 开始训练迭代
    for i in range(numIt):
    
        # 获取最佳单层决策树，最小错误率和估计的类别向量
        bestStump,error,classEst = buildStump(dataArr,classLabels,D)
        
        # 计算alpha值，为本次分类器输出结果的权重
        alpha = float(0.5*log((1.0-error)/max(error,1e-16)))
        bestStump['alpha'] = alpha
        
        # 将本次获得的决策树存入分类器列表
        weakClassArr.append(bestStump)
        
        # 计算下一次迭代的D值
        expon = multiply(-1*alpha*mat(classLabels).T,classEst) 
        D = multiply(D,exp(expon))
        D = D/D.sum()
        
        # 计算总分类器的错误率，aggClassEst保存运行时的类型估计值
        aggClassEst += alpha*classEst
        aggErrors = multiply(sign(aggClassEst) != mat(classLabels).T,ones((m,1)))
        errorRate = aggErrors.sum()/m
        print "total error: ",errorRate
        
        # 如果错误率已经为0则退出迭代
        if errorRate == 0.0: break
    
    # 返回弱分类器
    return weakClassArr

# AdaBoost分类函数，根据训练得到的弱分类器对数据进行分类
def adaClassify(datToClass,classifierArr):
    # 初始化输入数据
    dataMatrix = mat(datToClass)
    m = shape(dataMatrix)[0]
    
    # 初始化aggClassEst为全0向量
    aggClassEst = mat(zeros((m,1)))
    
    # 依次使用每个弱分类器
    for i in range(len(classifierArr)):
    
        # 使用弱分类器得到类别估计值
        classEst = stumpClassify(dataMatrix,classifierArr[i]['dim'],\
                                 classifierArr[i]['thresh'],\
                                 classifierArr[i]['ineq'])
        
        # 输出的类别乘以权重后累加到aggClassEst上
        aggClassEst += classifierArr[i]['alpha']*classEst
    
    # 返回sign()函数处理过的aggClassEst值即为预计的类别
    return sign(aggClassEst)
    
# 测试算法
def adaTest():
    # 读取训练数据
    trainingSet, trainingLabels = loadDataSet('horseColicTraining2.txt')
    
    # 训练算法
    classifierArray = adaBoostTrainDS(trainingSet, trainingLabels)

    # 测试条目数量
    numTestVec = 67.0
    
    # 读取测试数据
    testSet, testLabels = loadDataSet('horseColicTest2.txt')
    
    # 使用分类器进行预测
    prediction10 = adaClassify(testSet, classifierArray)
    
    # 对比分类预测结果及真实结果并记录错误率
    errArr = mat(ones((67,1)))
    
    # 矩阵计算得到判断出错的条目数
    errorCount = errArr[prediction10!=mat(testLabels).T].sum()
    
    # 计算并输出错误率
    errorRate = (float(errorCount)/numTestVec)
    print "the error rate of this test is: %f" % errorRate
    return errorRate
    
    
adaTest()
    
    