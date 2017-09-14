#-*- coding: utf-8 -*-

'''
Created on Oct 19, 2010

@author: Peter
'''

from numpy import *

import re

# 读取邮件数据
def loadEmailFile(filename):
    fr = open(filename)
    
    # 使用正则表达式进行分词
    regEx = re.compile('\\W*')
    wordList = regEx.split(fr.read())
    return [tok.lower() for tok in wordList if len(tok) > 2]
    
# 创建词汇表
def createVocabList(docList):
    # 初始化集合
    vocabSet = set([])
    
    # 遍历docList，提取所有出现过的单词
    for document in docList:
        # 集合操作
        vocabSet = vocabSet | set(document)
    return list(vocabSet)
    
# 创建朴素贝叶斯词袋模型
def bagOfWords2VecMN(vocabList, inputSet):
    # 初始化词向量，每个元素对应词汇表中的一个单词，初始化为0
    returnVec = [0]*len(vocabList)
    
    # 遍历输入的邮件数据，每遇到一个词，词向量中对应的值加1
    for word in inputSet:
        if word in vocabList:
            returnVec[vocabList.index(word)] += 1
    return returnVec
    
# 朴素贝叶斯算法训练函数
def trainNB0(trainMatrix,trainCategory):
    # 文档数量
    numTrainDocs = len(trainMatrix)
    # 数据集中的词汇量
    numWords = len(trainMatrix[0])
    
    # 初始化
    # 计算垃圾邮件在邮件总数中出现的概率
    pAbusive = sum(trainCategory)/float(numTrainDocs)
    p0Num = ones(numWords); p1Num = ones(numWords)
    p0Denom = 2.0; p1Denom = 2.0 
    
    # 遍历每篇文档
    for i in range(numTrainDocs):
        # 判断是否是垃圾邮件
        if trainCategory[i] == 1:
            # 如果是垃圾邮件
            # 向量加法增加每个单词在p1Num向量中出现的频次
            p1Num += trainMatrix[i]
            # 增加垃圾邮件所有词条的总计数p1Denom
            p1Denom += sum(trainMatrix[i])
        else:
            # 如果是正常邮件
            # 向量加法增加每个单词在p0Num向量中出现的频次
            p0Num += trainMatrix[i]
            # 增加正常邮件所有词条的总计数p0Denom
            p0Denom += sum(trainMatrix[i])
    
    # 返回每个词条在垃圾邮件中出现的概率向量
    p1Vect = log(p1Num/p1Denom)
    
    # 返回每个词条在正常邮件中出现的概率向量
    p0Vect = log(p0Num/p0Denom)

    return p0Vect,p1Vect,pAbusive
    
# 分类器
def classifyNB(vec2Classify, p0Vec, p1Vec, pClass1):
    # 按照朴素贝叶斯算法公式计算属于垃圾邮件或正常邮件的概率
    p1 = sum(vec2Classify * p1Vec) + log(pClass1)
    p0 = sum(vec2Classify * p0Vec) + log(1.0 - pClass1)
    if p1 > p0:
        # 如果垃圾邮件的概率大则返回垃圾邮件分类
        return 1
    else: 
        return 0

# 垃圾邮件朴素贝叶斯分类器测试
def spamTest():
    # 初始化邮件词汇列表，分类向量，全文列表等
    docList=[]; classList = []; fullText =[]
    
    # 遍历读取所有的邮件文件
    for i in range(1,26):
        # 读取所有垃圾邮件
        wordList = loadEmailFile('email/spam/%d.txt' % i)
        # 注意python列表中append和extend的区别
        docList.append(wordList)
        fullText.extend(wordList)
        classList.append(1)
        # 读取所有正常邮件
        wordList = loadEmailFile('email/ham/%d.txt' % i)
        docList.append(wordList)
        fullText.extend(wordList)
        classList.append(0)
    # 获取单词表
    vocabList = createVocabList(docList)
    
    # 初始化训练数据集和测试数据集
    trainingSet = range(50); testSet=[]
    for i in range(10):
        # 从中随机提取10封作为测试集
        randIndex = int(random.uniform(0,len(trainingSet)))
        testSet.append(trainingSet[randIndex])
        # 将测试邮件从训练集中删除
        del(trainingSet[randIndex])
    
    # 构建训练算法所需要的输入参数
    trainMat=[]; trainClasses = []
    for docIndex in trainingSet:
        trainMat.append(bagOfWords2VecMN(vocabList, docList[docIndex]))
        trainClasses.append(classList[docIndex])
    
    # 执行训练算法，获得概率向量
    p0V,p1V,pSpam = trainNB0(array(trainMat),array(trainClasses))
    
    # 进行分类器测试
    errorCount = 0
    
    # 循环读取测试邮件
    for docIndex in testSet:
        # 获得词袋
        wordVector = bagOfWords2VecMN(vocabList, docList[docIndex])
        
        # 分类后与实际类别进行对比
        if classifyNB(array(wordVector),p0V,p1V,pSpam) != classList[docIndex]:
            errorCount += 1
            print "classification error",docList[docIndex]
    # 打印错误率
    print 'the error rate is: ',float(errorCount)/len(testSet)
    
spamTest()
