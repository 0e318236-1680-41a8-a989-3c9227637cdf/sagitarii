#
# FUNÇÕES DA CLASSIFICAÇÃO DE ESTRELAS E GALÁXIAS
#
#
# REMOCAO DE OUTLIERS
#
remove_outliers <- function(x.train)
{
  q <- as.data.frame(lapply(x.train[-ncol(x.train)], quantile))
  i <- ncol(x.train)-1
  for (h in 1:i)
  {
    IQR <- q[4,h] - q[2,h]
    q1 <- q[2,h] - 3*IQR
    q2 <- q[4,h] + 3*IQR
    cond <- x.train[,h] >= q1 & x.train[,h] <= q2
    x.train <- x.train[cond,]
    return (x.train)
  }
}
#
# NORMALIZACAO MIN-MAX
#
normalize_minmax <- function(x.train, x.test=NA)
{
  if(is.na(x.test))
  {
    normalize_mm1 <- function(x.train)
    {
      return ((x.train-min(x.train))/(max(x.train)-min(x.train)))
    }
    x.mm <- as.data.frame(lapply(x.train, normalize_mm1))
  }
  else
  {
    normalize_mm2 <- function(x.test)
    {
      vmin <- x.test[length(x.test)-1]
      vmax <- x.test[length(x.test)]
      return ((x.test-vmin)/(vmax-vmin))
    }
    xmin <- apply(x.train, 2, min)
    xmax <- apply(x.train, 2, max)
    x.test <- rbind(x.test, xmin)
    x.test <- rbind(x.test, xmax)
    x.mm <- as.data.frame(lapply(x.test, normalize_mm2))
    temp1 <- nrow(x.mm)
    temp2 <- nrow(x.mm)-1
    x.mm <- x.mm[-temp1,]
    x.mm <- x.mm[-temp2,]
  }
  return (x.mm)
}
#
# NORMALIZACAO Z-SCORE
#
normalize_zscore <- function(x.train, x.test=NA)
{
  if(is.na(x.test))
  {
    x.zs <- as.data.frame(scale(x.train))
  }
  else
  {
    normalize_zs2 <- function(x.test)
    {
      vmean <- x.test[length(x.test)-1]
      vsd <- x.test[length(x.test)]
      return ((x.test-vmean)/vsd)
    }
    xmean <- apply(x.train, 2, mean)
    xsd <- apply(x.train, 2, sd)
    x.test <- rbind(x.test, xmean)
    x.test <- rbind(x.test, xsd)
    x.zs <- as.data.frame(lapply(x.test, normalize_zs2))
    temp1 <- nrow(x.zs)
    temp2 <- nrow(x.zs)-1
    x.zs <- x.zs[-temp1,]
    x.zs <- x.zs[-temp2,]
  }
  return (x.zs)
}
#
# FORWARD STEPWISE SELECTION
#
fss <- function(x.train, classe)
{
  library(leaps)
  
  selec_col <- function(cols)
  {
    n <- names(cols)
    n1 <- n[-1]
    return (n1)
  }
  regfit.fwd <- regsubsets(classe, x.train, nvmax=ncol(x.train)-1, method="forward")  
  summary(regfit.fwd)
  reg.summaryfwd <- summary(regfit.fwd)
  b1 <- which.max(reg.summaryfwd$adjr2)
  t <- coef(regfit.fwd,b1)
  vec <- selec_col(t)
  return (list(x.train[,vec], vec))
}
#
# LASSO
#
lasso <- function(x.train, classe)
{
  library(glmnet)
  
  m <- model.matrix(classe, x.train)[,-1]  
  n <- x.train[,ncol(x.train)]
  #set.seed(1)
  train <- sample(1:nrow(m),nrow(m)/2)
  test <- (-train)
  grid =10^ seq (10,-2, length =100)
  lasso.mod <- glmnet(m[train,],n[train],alpha=1,lambda=grid)
  cv.out =cv.glmnet (m[train,],n[train],alpha =1)
  bestlam =cv.out$lambda.min
  lasso.pred=predict (lasso.mod ,s=bestlam ,newx=m[test ,])
  out <- glmnet(m,n,alpha=1,lambda=grid)
  lasso.coef=predict (out,type ="coefficients", s=bestlam)
  l <- lasso.coef[(lasso.coef[,1])!=0,0]
  vec <- rownames(l)[-1]
  return (list(x.train[,vec], vec))
}
#
# PCA
#
pca <- function(x.train, vect=NA, pcameuindex=NA)
{
  if(is.na(vect))
  {
    xmeu <- x.train[, -ncol(x.train)]
  }
  else
  {
    xmeu <- x.train[, vect]
  }
  pcameu <- prcomp(xmeu, center=TRUE, scale.=TRUE)
  if(is.na(pcameuindex))
  {
    cumvar <- cumsum(pcameu$sdev^2/sum(pcameu$sdev^2))
    pcameuindex <- min(which(cumvar>0.9))
  }
  xpca <- array(0, dim=c(nrow(xmeu),pcameuindex))
  M <- as.matrix(xmeu)
  N <- as.matrix(pcameu$rotation[, 1:pcameuindex])
  xpca <- M %*% N
  xpca_df <- as.data.frame(xpca)
  x.train <- data.frame(xpca_df, x.train[, ncol(x.train)])
  return (list(x.train,pcameuindex))
}
#
# REDES NEURAIS COM PARAMETROS FIXOS
#
rn <- function(x.train, x.test)
{
  require(nnet)
  
  data.train <- x.train[,1:ncol(x.train)-1]
  alvo <- x.train[,ncol(x.train)]
  alvo.class <- class.ind(alvo)
  data.test <- x.test[,1:ncol(x.test)-1]
  tnet <- nnet(data.train, alvo.class, size=10, decay=5e-4, maxit=200)
  pnet <- predict(tnet, data.test, type="raw")
  return (pnet)
}
#
# REDES NEURAIS COM ESCOLHA DE PARAMETROS
#
rn2 <- function(x.train, x.test, sz, dc, it)
{
  require(nnet)
  
  data.train <- x.train[,1:ncol(x.train)-1]
  alvo <- x.train[,ncol(x.train)]
  alvo.class <- class.ind(alvo)
  data.test <- x.test[,1:ncol(x.test)-1]
  tnet <- nnet(data.train, alvo.class, size=sz, decay=dc, maxit=it)
  pnet <- predict(tnet, data.test, type="raw")
  return (pnet)
}
#
# SVM COM PARAMETROS FIXOS
#
svm <- function(x.train, x.test, alvo)
{
  require(kernlab)
  
  #data.train <- x.train[,1:ncol(x.train)-1]
  #alvo <- x.train[,ncol(x.train)]
  #alvo <- as.data.frame(alvo)
  #colnames(alvo) <- "alvo"
  data.test <- x.test[,1:ncol(x.test)-1]
  rbf <- rbfdot(sigma=0.1)
  #tsvm <- ksvm(alvo~., data=x.train, kernel=rbf, C=10, type="C-bsvc", prob.model=TRUE)
  tsvm <- ksvm(alvo, data=x.train, kernel=rbf, C=10, type="C-bsvc", prob.model=TRUE)
  psvm <- predict(tsvm, data.test, type="probabilities")
  return (psvm)
}
#
# SVM COM ESCOLHA DE PARAMETROS
#
svm2 <- function(x.train, x.test, alvo, kn, c, tp)
{
  require(kernlab)
  
  #data.train <- x.train[,1:ncol(x.train)-1]
  #alvo <- x.train[,ncol(x.train)]
  #alvo <- as.data.frame(alvo)
  #colnames(alvo) <- "alvo"
  data.test <- x.test[,1:ncol(x.test)-1]
  #rbfdot <- rbfdot(sigma=0.1)
  #tsvm <- ksvm(alvo~., data=x.train, kernel=rbf, C=10, type="C-bsvc", prob.model=TRUE)
  tsvm <- ksvm(alvo, data=x.train, kernel=kn, C=c, type=tp, prob.model=TRUE)
  psvm <- predict(tsvm, data.test, type="probabilities")
  return (psvm)
}
#
# AVALIA REDES NEURAIS
#
avalia_rn <- function(pred, alvo.teste)
{
  ntrueest <- 0
  ntruegal <- 0
  nfalseest <- 0
  nfalsegal <- 0
  tabela <- round(table(alvo.teste))
  for (i in 1:length(alvo.teste))
  {
    obj <- ifelse(pred[i,1] > 0.9, 1, 2)
    if ((alvo.teste[i] - obj) == 0)
    {
      if (obj == 1)
      {
        ntruegal <- ntruegal + 1
      }
      if (obj == 2)
      {
        ntrueest <- ntrueest + 1
      }
    }
    else
    {
      if (obj == 1)
      {
        nfalsegal <- nfalsegal + 1
      }
      if (obj == 2)
      {
        nfalseest <- nfalseest + 1
      }
    }
  }
  compl_gal <- (ntruegal/tabela[1])*100
  pur_gal <- (ntruegal/(ntruegal+nfalsegal))*100
  compl_est <- (ntrueest/tabela[2])*100
  pur_est <- (ntrueest/(ntrueest+nfalseest))*100
  sprintf("Completeza_galaxias=%.2f Pureza_galaxias=%.2f Completeza_estrelas=%.2f Pureza_estrelas=%.2f", compl_gal, pur_gal, compl_est, pur_est)
  #return (list(compl_gal, pur_gal, compl_est, pur_est))
}
#
# CURVA ROC
#
croc <- function(prob, label)
{
  require(ROCR)
  
  pred <- prediction(prob, label)
  perf <- performance(pred, "tpr", "fpr")
  plot(perf)
  auc <- performance(pred, "auc")
  return (auc)
}