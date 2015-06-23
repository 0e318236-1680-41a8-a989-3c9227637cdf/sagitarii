#!/usr/bin/env Rscript
#---- Deixar trecho abaixo comentado no Sagitarii
#sagitariiWorkFolder <- "D:/galaxy/namespaces/GALAXY/9C03F4EE-1CA3-438/4A567B83/9CBCDC4B-BF0B-4/6BB71380"
#setwd('d:/galaxy')


# ----------- SAGITARII REQUIREMENTS ---------------------------------------
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

x.train <- data.tr
x.test <- data.ts

source("classifica.R")

messageToSagitarii <- "starting..."

x.train.clean <- remove_outliers(x.train)
x.train.normmm <- normalize_minmax(x.train.clean)
set.seed(1)
x.train.lassomm <- lasso(x.train.normmm, x.train.normmm$alvo~.)
x.train.lassomm[[1]]$alvo <- x.train.normmm$alvo
x.test.normmm <- normalize_minmax(x.train,x.test)
x.test.lassomm <- x.test.normmm[,x.train.lassomm[[2]]]
x.test.lassomm$alvo <- x.test.normmm$alvo

tab$resultado <- "classifica.csv"
if (metodo=="rn") {
	x.rn2 <- rn2(x.train.lassomm[[1]], x.test.lassomm, tamanho, par_r, par_i)
	aa <- croc(x.rn2[,2], x.test.normmm$alvo)
	aa <- unlist(slot(aa, "y.values"))
	write.table(x.rn2, file=outpuClassifica, row.names=FALSE, quote = FALSE, sep=",")
}
write.table(tab, file=outputFile, row.names=FALSE, quote = FALSE, sep=",")
messageToSagitarii <- "done"
