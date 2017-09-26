# Before calling read.csv make sure to set working directory properly.
setwd("/2016 MS FALL/Courses/CSE5334 Data Mining/Assignments/Final/Assignment 7 - 1001444167")
# Read the data from the csv input file
data<-read.csv("yacht.csv", header = T, sep = ",")
summary(data)

# Converting numeric class into factors
library(arules)
data$ResiduaryResistance<-discretize(data$ResiduaryResistance, categories=3, method = "frequency", labels = c("Low","Moderate","High"))
summary(data)

# Plot the initial graph between FroudeNum & PrismaticCoefficient
plot(data$FroudeNum, data$PrismaticCoefficient, col = data$ResiduaryResistance)

# Partitioning the data into training data and testing data
sam<-sample(308, 270)
columns<-c('FroudeNum','PrismaticCoefficient','ResiduaryResistance')
training_data<-data[sam,columns]
testing_data<-data[-sam,columns]

# Create SVM model
svm_model<-svm(ResiduaryResistance~., data = training_data, kernel = "linear", cost = 10, scale = FALSE)
print(svm_model)
plot(svm_model, training_data[,columns])

# Tune the svn model to get the optimal cost parameter to be used for svm model
tune<-tune(svm, ResiduaryResistance~., data = training_data, kernel = "linear", ranges = list(cost=c(0.001,0.01,0.1,1,10,100)))
summary(tune)

# Calculate prediction error in predicting class for testing data
library(caret)
pred<-predict(svm_model, testing_data, type = "class")
plot(pred)
matrix<-table(pred, testing_data$ResiduaryResistance)
1-sum(diag((matrix)))/sum(matrix)
confusionMatrix(matrix)
