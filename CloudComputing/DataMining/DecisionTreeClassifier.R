## Install the required packages
install.packages("rpart")
install.packages("rpart.plot")

## Import library rpart and rpart.plot
library(rpart)
library(rpart.plot)

# Before calling read.csv make sure to set working directory properly.
# setwd("/2016 MS FALL/Courses/CSE5334 Data Mining/Assignments/Final/Assignment 6 - 1001444167")
## Read the data from the csv input file
data<-read.csv("yacht.csv", header = T, sep = ",")

## Partitioning the data into training.data(80%) data and testing.data(20%)
set.seed(682)
partitions<-sample(2, nrow(data), replace = T, prob = c(0.8,0.2))
training.data<-data[partitions==1,]
testing.data<-data[partitions==2,]

## Building a decision tree using rpart library
tree<-rpart(ResiduaryResistance~.,training.data)
rpart.plot(tree, extra = 1)

## Predicting the ResiduaryResistance of the data
predicted.training.values<-predict(tree,training.data)
predicted.testing.values<-predict(tree,testing.data)

## misclassification error on training data
predicted.training.values<-cut(predicted.training.values, b = 3, labels = c("Low","Moderate","High"))
actual.values<-cut(training.data$ResiduaryResistance, b = 3, labels = c("Low","Moderate","High"))
training.table<-table(predicted.training.values, actual.values)
1-sum(diag((training.table)))/sum(training.table)

## misclassification error on testing data
predicted.testing.values<-cut(predicted.testing.values, b = 3, labels = c("Low","Moderate","High"))
actual.values<-cut(testing.data$ResiduaryResistance, b = 3, labels = c("Low","Moderate","High"))
testing.table<-table(predicted.testing.values, actual.values)
1-sum(diag((testing.table)))/sum(testing.table)
