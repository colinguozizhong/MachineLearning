#-*- coding: utf-8 -*-

'''
Created on Jan 8, 2011

@author: Peter
'''

from numpy import *
from BeautifulSoup import BeautifulSoup

# 从页面读取数据，生成retX和retY列表
def scrapePage(retX, retY, inFile, yr, numPce, origPrc):

    # 打开并读取HTML文件
    fr = open(inFile);
    soup = BeautifulSoup(fr.read())
    i=1
    
    # 根据HTML页面结构进行解析
    currentRow = soup.findAll('table', r="%d" % i)
    while(len(currentRow)!=0):
        currentRow = soup.findAll('table', r="%d" % i)
        title = currentRow[0].findAll('a')[1].text
        lwrTitle = title.lower()
        
        # 查找是否有全新标签
        if (lwrTitle.find('new') > -1) or (lwrTitle.find('nisb') > -1):
            newFlag = 1.0
        else:
            newFlag = 0.0
        
        # 查找是否已经标志出售，我们只收集已出售的数据
        soldUnicde = currentRow[0].findAll('td')[3].findAll('span')
        if len(soldUnicde)==0:
            print "item #%d did not sell" % i
        else:
            # 解析页面获取当前价格
            soldPrice = currentRow[0].findAll('td')[4]
            priceStr = soldPrice.text
            priceStr = priceStr.replace('$','') #strips out $
            priceStr = priceStr.replace(',','') #strips out ,
            if len(soldPrice)>1:
                priceStr = priceStr.replace('Free shipping', '')
            sellingPrice = float(priceStr)
            
            # 去掉不完整的套装价格
            if  sellingPrice > origPrc * 0.5:
                    print "%d\t%d\t%d\t%f\t%f" % (yr,numPce,newFlag,origPrc, sellingPrice)
                    retX.append([yr, numPce, newFlag, origPrc])
                    retY.append(sellingPrice)
        i += 1
        currentRow = soup.findAll('table', r="%d" % i)
        
# 依次读取六种乐高套装的数据，并生成数据矩阵        
def setDataCollect(retX, retY):
    scrapePage(retX, retY, '/home/shiyanlou/mylab10/setHtml/lego8288.html', 2006, 800, 49.99)
    scrapePage(retX, retY, '/home/shiyanlou/mylab10/setHtml/lego10030.html', 2002, 3096, 269.99)
    scrapePage(retX, retY, '/home/shiyanlou/mylab10/setHtml/lego10179.html', 2007, 5195, 499.99)
    scrapePage(retX, retY, '/home/shiyanlou/mylab10/setHtml/lego10181.html', 2007, 3428, 199.99)
    scrapePage(retX, retY, '/home/shiyanlou/mylab10/setHtml/lego10189.html', 2008, 5922, 299.99)
    scrapePage(retX, retY, '/home/shiyanlou/mylab10/setHtml/lego10196.html', 2009, 3263, 249.99)

# 计算给定lambda值得回归系数
def ridgeRegres(xMat,yMat,lam=0.2):
    # 使用矩阵运算实现146页的回归系数计算公式
    xTx = xMat.T*xMat
    denom = xTx + eye(shape(xMat)[1])*lam
    
    # 判断是否为奇异矩阵
    if linalg.det(denom) == 0.0:
        print "This matrix is singular, cannot do inverse"
        return
    ws = denom.I * (xMat.T*yMat)
    return ws

# 计算回归系数矩阵
def ridgeTest(xArr,yArr):
    # 初始化X和Y矩阵
    xMat = mat(xArr); yMat=mat(yArr).T
    
    # 对X和Y矩阵进行标准化
    # 计算所有特征的均值
    yMean = mean(yMat,0)
    
    # 特征值减去各自的均值
    yMat = yMat - yMean
    
    # 标准化X矩阵数据
    # 获得均值
    xMeans = mean(xMat,0)
    # 获得方差
    xVar = var(xMat,0)
    # 标准化方法：减去均值除以方差
    xMat = (xMat - xMeans)/xVar
    
    # 计算回归系数30次
    numTestPts = 30
    wMat = zeros((numTestPts,shape(xMat)[1]))
    for i in range(numTestPts):
        ws = ridgeRegres(xMat,yMat,exp(i-10))
        wMat[i,:]=ws.T
    return wMat

# 交叉验证测试岭回归
def crossValidation(xArr,yArr,numVal=10):
    # 获得数据点个数，xArr和yArr具有相同长度
    m = len(yArr)
    indexList = range(m)
    errorMat = zeros((numVal,30))
    
    # 主循环 交叉验证循环
    for i in range(numVal):
        # 随机拆分数据，将数据分为训练集（90%）和测试集（10%）
        trainX=[]; trainY=[]
        testX = []; testY = []
        
        # 对数据进行混洗操作
        random.shuffle(indexList)
        
        # 切分训练集和测试集
        for j in range(m):
            if j < m*0.9: 
                trainX.append(xArr[indexList[j]])
                trainY.append(yArr[indexList[j]])
            else:
                testX.append(xArr[indexList[j]])
                testY.append(yArr[indexList[j]])
        
        # 获得回归系数矩阵
        wMat = ridgeTest(trainX,trainY)
        
        # 循环遍历矩阵中的30组回归系数
        for k in range(30):
            # 读取训练集和数据集
            matTestX = mat(testX); matTrainX=mat(trainX)
            # 对数据进行标准化
            meanTrain = mean(matTrainX,0)
            varTrain = var(matTrainX,0)
            matTestX = (matTestX-meanTrain)/varTrain
            
            # 测试回归效果并存储
            yEst = matTestX * mat(wMat[k,:]).T + mean(trainY)
            
            # 计算误差
            errorMat[i,k] = ((yEst.T.A-array(testY))**2).sum()
    
    # 计算误差估计值的均值
    meanErrors = mean(errorMat,0)
    minMean = float(min(meanErrors))
    bestWeights = wMat[nonzero(meanErrors==minMean)]
    
    # 不要使用标准化的数据，需要对数据进行还原来得到输出结果
    xMat = mat(xArr); yMat=mat(yArr).T
    meanX = mean(xMat,0); varX = var(xMat,0)
    unReg = bestWeights/varX
    
    # 输出构建的模型
    print "the best model from Ridge Regression is:\n",unReg
    print "with constant term: ",-1*sum(multiply(meanX,unReg)) + mean(yMat)

lgX = []
lgY = []

setDataCollect(lgX, lgY)
crossValidation(lgX, lgY, 10)
    