#!/usr/bin/env Rscript

#---- Deixar trecho abaixo comentado no Sagitarii
#sagitariiWorkFolder <- "D:/runpot/namespaces/GALAXY/41F83AB5-829A-498/B726D80A/7B69B04D-AFCF-4/GALAXY"


# ----------- SAGITARII REQUIREMENTS : DO NOT MODIFY ------------------------
inputFileFolder <- paste( sagitariiWorkFolder, "inbox", sep = "/")
outputFileFolder <- paste( sagitariiWorkFolder, "outbox", sep = "/")
paramFile <- paste( sagitariiWorkFolder, "sagi_input.txt", sep = "/")
outputFile <- paste( sagitariiWorkFolder, "sagi_output.txt", sep = "/")
outpuClassifica <- paste( outputFileFolder, "classifica.csv", sep = "/")
setwd(libraryFolder)
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

tab$resultado <- "classifica.csv"
tab$resultadov <- 0

write.table(tab, file=outpuClassifica, row.names=FALSE, quote = FALSE, sep = ",")
write.table(tab, file=outputFile, row.names=FALSE, quote = FALSE, sep = ",")
