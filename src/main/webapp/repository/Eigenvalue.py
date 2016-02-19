## geni = invariants generator
## função que calcula os valores dos invariantes de grafos
## Parâmetros: 
## arquivo:  arquivo texto com a matriz de adjacência gerada pelo geng
## args: string contendo os caracteres correspondentes 
## das funções a serem geradas
##        -a   : adjacency matrix
##        -l   : laplacian matrix
##        -q   : signless laplacian matrix
##        -x   : adjacency matrix of the complement graph
##        -y   : laplacian matrix of the complement graph
##        -z   : signless laplacian matrix of the complement graph
##

import getopt
import csv
import mathchem
import ntpath

def Eigenvalue(outputdir,inputfile, args):
	mols = mathchem.read_from_g6(inputfile)
	arquivo = path_leaf(inputfile)
	l = mols[0].adjacency_matrix()
	l = Matrix(l)
	G = Graph(l)
	args = args.split()
	optlist, args = getopt.getopt(args, 'alqxyz')
	AdjacencyMatrixNeeded = False
	AdjacencyMatrixComplementNeeded = False
	LaplacianMatrixNeeded = False
	LaplacianMatrixComplementNeeded = False
	SignLaplacianMatrixNeeded = False
	SignLaplacianMatrixComplementNeeded = False
	
	for o, a in optlist:
		if o == "-a":
			AdjacencyMatrixNeeded = True
		elif o == "-l":
			LaplacianMatrixNeeded = True
		elif o == "-q":
			SignLaplacianMatrixNeeded = True
		elif o == "-x":
			AdjacencyMatrixComplementNeeded = True
		elif o == "-y":
			LaplacianMatrixComplementNeeded = True
		elif o == "-z":
			SignLaplacianMatrixComplementNeeded = True		    
		else:
			assert False, "unhandled option"
	
	if AdjacencyMatrixComplementNeeded or LaplacianMatrixComplementNeeded or SignLaplacianMatrixComplementNeeded:
	   GC = G.complement()
	
	if AdjacencyMatrixNeeded == True:
		EigAdjacency = G.spectrum()
		EigAdjacency.sort()
		foutAdj = open(outputdir+arquivo+'.adj','w')
		for k in EigAdjacency:
			foutAdj.write(str(k)+'\n')
		foutAdj.close()	
		
	if AdjacencyMatrixComplementNeeded == True:
		EigAdjacencyBar = GC.spectrum()
		EigAdjacencyBar.sort()
		foutAdjBar = open(outputdir+arquivo+'.adjb','w')
		for k in EigAdjacencyBar:
			foutAdjBar.write(str(k)+'\n')
		foutAdjBar.close()	
	
	if LaplacianMatrixNeeded == True:
		LaplacianMatrix = G.laplacian_matrix()
		EigLaplacian = LaplacianMatrix.eigenvalues()
		EigLaplacian.sort()
		foutLap = open(outputdir+arquivo+'.lap','w')
		for k in EigLaplacian:
			foutLap.write(str(k)+'\n')	
		foutLap.close()
	
	if LaplacianMatrixComplementNeeded == True:
		LaplacianMatrixBar = GC.laplacian_matrix()
		EigLaplacianBar = LaplacianMatrixBar.eigenvalues()
		EigLaplacianBar.sort()	
		foutLapBar = open(outputdir+arquivo+'.lapb','w')
		for k in EigLaplacianBar:
			foutLapBar.write(str(k)+'\n')
		foutLapBar.close()	
		
	if SignLaplacianMatrixNeeded == True:
		SignlessLaplacianMatrix = 2*G.adjacency_matrix() + G.laplacian_matrix()
		EigSignless = SignlessLaplacianMatrix.eigenvalues()
		EigSignless.sort()
		foutSignLap = open(outputdir+arquivo+'.sgnlap','w')
		for k in EigSignless:
			foutSignLap.write(str(k)+'\n')
		foutSignLap.close()
		
	if SignLaplacianMatrixComplementNeeded == True:
		SignlessLaplacianMatrixBar = 2*GC.adjacency_matrix() + GC.laplacian_matrix()
		EigSignlessBar = SignlessLaplacianMatrixBar.eigenvalues()
		EigSignlessBar.sort()
		foutSignLapBar = open(outputdir+arquivo+'.sgnlapb','w') 
		for k in EigSignlessBar:
			foutSignLapBar.write(str(k)+'\n')
		foutSignLapBar.close()	
		

def path_leaf(path):
    head, tail = ntpath.split(path)
    return tail or ntpath.basename(head)