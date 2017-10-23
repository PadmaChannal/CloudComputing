# Before calling read.csv make sure to set working directory properly.
# setwd("/2016 MS FALL/Courses/CSE5334 Data Mining/Assignments/Final/Assignment 6 - 1001444167")
# Read the data from the csv input file
data<-read.csv("yacht.csv", header = T, sep = ",")

cor(data$ResiduaryResistance,data$FroudeNum)

# Plot the scatter plot for ResiduaryResistance and FroudeNum
plot(ResiduaryResistance~FroudeNum, data)

# Display the mean ResiduaryResistance on the plot
mean.resistance<-mean(data$ResiduaryResistance)
abline(h=mean.resistance, col="green")

# Generate a linear regression model
model<-lm(ResiduaryResistance~FroudeNum, data)
abline(model, col="cyan", lwd=3)
summary(model)

# Plot the linear regression model to get the different graphs
plot(model)

#References
#[1] Jaiwei Han, Micheline Kamber, Jian Pei, Data Mining – Concepts and Techniques
#[2] http://archive.ics.uci.edu/ml/datasets/Yacht+Hydrodynamics
#[3] https://www.youtube.com/watch?v=Xh6Rex3ARjc

