# Before calling read.csv make sure to set working directory properly.
setwd("/2016 MS FALL/Courses/CSE5334 Data Mining/Assignments/Final/Assignment 7 - 1001444167")
# Read the data from the csv input file
data<-read.csv("yacht.csv", header = T, sep = ",")

# Mixing up the data
set.seed(1232)
randomize<-runif(nrow(data))
data<-data[order(randomize),]

# Normalize the data
normalize<-function(x) {
  return ((x-min(x))/(max(x)-min(x)))}

normalized_data<-as.data.frame(lapply(data[,c(1,2,3,4,5,6)],normalize))
summary(normalized_data)

# Partitioning the data into training data and testing data
training_data<-normalized_data[1:270,]
testing_data<-normalized_data[271:308,]
training_target<-data[1:270,7]
testing_target<-data[271:308,7]
training_target<-cut(training_target, b = 3, labels = c("Low","Moderate","High"))
testing_target<-cut(testing_target, b = 3, labels = c("Low","Moderate","High"))

# Apply knn algorithm
require(class)
predicted<-knn(train = training_data, test = testing_data, cl=training_target, k=sqrt(nrow(data)))

# Calculate prediction error in predicting class for testing data
library(caret)
matrix<-table(testing_target, predicted)
1-sum(diag((matrix)))/sum(matrix)
confusionMatrix(matrix)

# Plot the predicted values by knn model
plot(predicted)

# Plot the nearest neighbour graph for training data
library(cccd)

