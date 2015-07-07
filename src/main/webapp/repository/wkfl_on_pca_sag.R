#!/usr/bin/env Rscript
#---- Deixar trecho abaixo comentado no Sagitarii
#sagitariiWorkFolder <- "D:/runpot/namespaces/GALAXY/41F83AB5-829A-498/B726D80A/7B69B04D-AFCF-4/GALAXY"
#setwd("D:/runpot/wrappers")

# ----------- SAGITARII REQUIREMENTS : DO NOT MODIFY ------------------------
inputFileFolder <- paste( sagitariiWorkFolder, "inbox", sep = "/")
outputFileFolder <- paste( sagitariiWorkFolder, "outbox", sep = "/")
paramFile <- paste( sagitariiWorkFolder, "sagi_input.txt", sep = "/")
outputFile <- paste( sagitariiWorkFolder, "sagi_output.txt", sep = "/")
outpuClassifica <- paste( outputFileFolder, "classifica.csv", sep = "/")
# ---------------------------------------------------------------------------

tab <- read.table( paramFile, header = TRUE, sep = ",")
metodo <- tab$metodo[1]
tamanho <- tab$tamanho[1]
par_r <- tab$par_r[1]
par_i <- tab$par_i[1]
arquivoTreino <- tab$treino[1]
arquivoTeste <- tab$teste[1]

trainFile <- paste( inputFileFolder, arquivoTreino, sep = "/")
load(trainFile)
testFile <- paste( inputFileFolder, arquivoTeste, sep = "/")
load(testFile)

source("classifica.R")

x.train <- remove_outliers(x.train)

x.train.raw <- x.train
x.train <- normalize_minmax(x.train.raw)
x.test <- normalize_minmax(x.train.raw,x.test)

if (FALSE) { # uso de lasso
  print("lasso")
  set.seed(1)
  x.list <- lasso(x.train, x.train$alvo~.)
  x.train <-x.list[[1]]
  x.train.columns <- x.list[[2]]
  x.test.alvo <- x.test$alvo
  x.test <- x.test[,x.train.columns]
  x.test$alvo <- x.test.alvo
}

if (TRUE) { # uso de pca
  print("pca")
  
  set.seed(1)
  x.train.raw <- x.train
  x.list <- pca(x.train.raw, varacum=0.2)
  x.train <-x.list[[1]]
  x.train.transf <- x.list[[2]]

  x.list <- pca(x.train.raw, test=x.test, transf = x.train.transf, varacum=0.2)
  x.test <- x.list[[1]]
  
  x.train.pca.raw <- x.train
  x.train <- normalize_minmax(x.train.pca.raw)
  x.test <- normalize_minmax(x.train.pca.raw,x.test)
}

tab$resultado <- 0

  if (metodo=="rn")
  {
    print("rn")
    set.seed(1)
    x.rn2 <- rn2(x.train, x.test, tamanho, par_r, par_i)
    aa <- croc(x.rn2[,2], x.test$alvo)
    aa <- unlist(slot(aa, "y.values"))
    tab$resultado[1] <- aa
    write.table(x.rn2, file=outpuClassifica, row.names=FALSE, quote = FALSE)
  } else if (metodo=="rbfdot_C-svc")
  {
    x.svm4 <- svm4(x.train, x.test, x.train$alvo~., rbfdot, par_i, C-svc)
    aa <- croc(x.svm4[,2], x.test$alvo)
    aa <- unlist(slot(aa, "y.values"))
    tab$resultado[1] <- aa
    write.table(x.svm4, file=outpuClassifica, row.names=FALSE, quote = FALSE)
  } else if (metodo=="rbfdot_nu-svc")
  {
    x.svm4 <- svm4(x.train, x.test, x.train$alvo~., rbfdot, par_i, nu-svc)
    aa <- croc(x.svm4[,2], x.test$alvo)
    aa <- unlist(slot(aa, "y.values"))
    tab$resultado[1] <- aa
    write.table(x.svm4, file=outpuClassifica, row.names=FALSE, quote = FALSE)
  } else if (metodo=="rbfdot_C-bsvc")
  {
    x.svm4 <- svm4(x.train, x.test, x.train$alvo~., rbfdot, par_i, C-bsvc)
    aa <- croc(x.svm4[,2], x.test$alvo)
    aa <- unlist(slot(aa, "y.values"))
    tab$resultado[1] <- aa
    write.table(x.svm4, file=outpuClassifica, row.names=FALSE, quote = FALSE)
  } else if (metodo=="knn")
  {
    x.knn <- knear(x.train, x.test, tamanho)
    aa <- croc(x.knn[,2], x.test$alvo)
    aa <- unlist(slot(aa, "y.values"))
    tab$resultado[1] <- aa
    write.table(x.knn, file=outpuClassifica, row.names=FALSE, quote = FALSE)
  } else if (metodo=="rf")
  {
    x.rf <- rf(x.train, x.test, tamanho)
    aa <- croc(x.rf[,2], x.test$alvo)
    aa <- unlist(slot(aa, "y.values"))
    tab$resultado[1] <- aa
    write.table(x.rf, file=outpuClassifica, row.names=FALSE, quote = FALSE)
  }
  write.table(tab, file=outputFile, row.names=FALSE, quote = FALSE)
