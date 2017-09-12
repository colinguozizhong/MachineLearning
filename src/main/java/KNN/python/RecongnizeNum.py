# -*- coding: utf-8 -*-
"""
Created on Sun Mar 15 15:51:15 2015

@author: bobo
"""

"""
转换图片
"""
import os 
import operator
from numpy import *
import sys
reload(sys)
sys.setdefaultencoding('utf8')

"""
将每张图片转换成向量形式
即把每张图片转换成一维1*1024矩阵形式
"""
def img2vector(filename):
    """
    filename代表文件名称
    """
    returnVector = zeros((1,1024))##声明一个0矩阵
    fr = open(filename)
    for i in range(32):
        lineStr = fr.readline()##°每一行文件
        for j in range(32):
            returnVector[0,32*i+j] = int(lineStr[j])##一共32行，全部存储到returnVector里面
    fr.close()
    return returnVector


def classify(inX,dataSet,labels,k):
    """
    四个参数，inX是测试向量，dataSet样本向量数据，labels是标签，k是选取前k个做评测
    tile(A,n)用于重复A矩阵n次    
    argsort()返回的是数组值从小到大的索引
    list.get(k,d)
    get()相当于一条if...else...语句,参数k在字典中，字典将返回list[k];如果参数k不在字典中则返回参数d,如果K在字典中则返回k对应的value值；
    例子：
    l = {5:2,3:4}
    print l.get(3,0)返回的值是4；
    Print l.get（1,0）返回值是0；(该例来源于网络)
    """
    dataSetSize = dataSet.shape[0]##shpe函数用于返回矩阵的长度，如shape[0]返回第一维矩阵长度，shape[1]返回第二维矩阵长度以此类推，还有其他功能执行查阅
    diffMat = tile(inX,(dataSetSize,1)) - dataSet##tile函数主要功能是重复矩阵多少次，重复了测试向量，与每一个样本相减
    sqDiffMat = diffMat**2##计算平方
    sqDistances = sqDiffMat.sum(axis = 1)##计算矩阵横轴的和
    distances = sqDistances**0.5##平方
    sortedDistIndicies = distances.argsort()##用argsort排序
    classCount = {}
    for i in range(k):
        voteLabel = labels[sortedDistIndicies[i]]##通过索引得到前该距离所属的类型
        classCount[voteLabel] = classCount.get(voteLabel,0)+1##相应的类型+1
    sortedClassCount = sorted(classCount.iteritems(),key = operator.itemgetter(1),reverse = True)
    return sortedClassCount[0][0]

"""
classTest()函数用于处理32*32的数据，
"""
def classTest():
    file_object = open('result.txt', 'w')
    Labels = []
    trainingFileList = os.listdir("./digits/trainingDigits/")##listdir函数用于获取该目录下的文件列表，并且以数组的方式存储
    length = len(trainingFileList)##获取数组长度
    trainingMat = zeros((length,1024))##声明一个length*1024的矩阵用于存储所有样本的向量形式
    for i in range(length):
        fileNameStr = trainingFileList[i]##获取列表中每一个文件名(包含扩展名)
        fileName = fileNameStr.split('.')[0]##获取列表中每一个文件名(不包含扩展名)
        numClass = fileName.split('_')[0]##获取该文件所属的类别（因为文件名都是以‘数字类别_第几个样本.txt’形式的，所以需要进行两次的split函数）
        Labels.append(numClass)##以队列的形式加入到Labels的队尾
        trainingMat[i,:] = img2vector("./digits/trainingDigits/"+ fileNameStr)##用img2vector()函数处理32*32的图片矩阵，存入trainingMat中
    testFileList = os.listdir("./digits/testDigits/")##测试组的文件列表，下面的代码意思如上，多余的就不写了
    ##erreCount = 0.0
    lengthTest = len(testFileList)
    for i in range(lengthTest):
        fileNameStr = testFileList[i]
        fileName = fileNameStr.split('.')[0]
        numClass = fileName.split('_')[0]
        vectorUnderTest = img2vector("./digits/testDigits/"+fileNameStr)
        classifierResult =  classify(vectorUnderTest,trainingMat,Labels,3)
        file_object.write(str(classifierResult)+"       "+str(numClass)+'\n')
        ##print  "come back result is %s.......real result is %s" %(classifierResult,numClass)
    file_object.close()
        
if __name__ == '__main__':
    classTest()
    print ("测试完成")