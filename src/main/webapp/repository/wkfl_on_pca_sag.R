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

tab$resultado <- "classifica.csv"
tab$resultadov <- 0

write.table(tab, file=outpuClassifica, row.names=FALSE, quote = FALSE, sep = ",")
write.table(tab, file=outputFile, row.names=FALSE, quote = FALSE, sep = ",")
