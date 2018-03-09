#########################################
# PMML test model                       #
# Alvaro Muir                           #
# Verizon IT Analytics Data Engineering #
# 03.06.18                              #
#########################################

rm(list=ls())
cat("\014")
options(width=240)
options(warn=-1)

library(randomForest)
library(XML)
library(pmml)

data(iris)

# split, train and report
ind <- sample(2,nrow(iris),replace=TRUE,prob=c(0.7,0.3))
training <- iris[ind==1,]
testing <- iris[ind==2,]
iris_rf <- randomForest(Species~.,data=training,ntree=100,proximity=TRUE)
table(predict(iris_rf),training$Species)

# viz
print(iris_rf)
attributes(iris_rf)
plot(iris_rf)

# export PMML
iris_rf.pmml <- pmml(iris_rf,name="Iris Random Forest",data=iris_rf)
saveXML(iris_rf.pmml,"iris_rf.pmml")
