## geni = invariants generator
## função que calcula os valores dos invariantes de grafos
## Parâmetros: 
## arquivo:  arquivo texto com a matriz de adjacência gerada pelo geng
## args: string contendo os caracteres correspondentes 
## das funções a serem geradas
##        -a   : número cromatico
##        -b   : número cromatico do grafo complementar
##        -c   : tamanho da maior clique
##        -d   : tamanho da maior clique do grafo complementar
##        -e k : k-ésimo maior grau do grafo
##        -f k : k-ésimo maior grau do grafo complementar
##
import getopt
import csv
import mathchem

from sage.graphs.graph_coloring import chromatic_number

def geni(arquivo, args):
	##f = open(arquivo,'r') #leitura do arquivo
	## l = Matrix ([ map(int,line.split(' ')) for line in f ]) ## matrix de adjacência do grafo
	mols = mathchem.read_from_g6(arquivo)
	l = mols[0].adjacency_matrix()
	l = Matrix(l)
	G = Graph(l)
	##f.close()
	args = args.split()
	optlist, args = getopt.getopt(args, 'abcde:f:')
	ChromaticNumberNeeded = False
	ChromaticNumberComplementNeeded = False
	LargestCliqueSizeNeeded = False
	LargestCliqueSizeComplementNeeded = False
	kLargestDegree = False
	kLargestDegreeComplement = False
	
	for o, a in optlist:
		if o == "-a":
			ChromaticNumberNeeded = True
		elif o == "-b":
			ChromaticNumberComplementNeeded = True
			Flag = 1
		elif o == "-c":
			LargestCliqueSizeNeeded = True
		elif o == "-d":
			LargestCliqueSizeComplementNeeded = True
		elif o == "-e":
			kLargestDegree = True
			ParamkLargestDegree = a
		elif o == "-f":
			kLargestDegreeComplement = True
			ParamkLargestDegreeComplement = a
		else:
			assert False, "unhandled option"
	
	if ChromaticNumberComplementNeeded or LargestCliqueSizeComplementNeeded or kLargestDegreeComplement:
	   GC = G.complement()
	## lc = G.complement().adjacency_matrix()
	## lista de saída dos parâmetros
	## lista.array('i',[0,0,0,0,0,0])
	lista = vector([0,0,0,0,0,0])
	## print lista[0]
	if ChromaticNumberNeeded == True:
		CN = G.chromatic_number()
		lista[0]=CN
	else:
		lista[0] = -1
	if ChromaticNumberComplementNeeded == True:
		CNC = GC.chromatic_number()
		lista[1] = CNC
	else:
		lista[1] = -1
	
	if LargestCliqueSizeNeeded == True:
		CS = G.clique_number()
		lista[2] = CS
	else:
		lista[2] = -1
	if LargestCliqueSizeComplementNeeded == True:
		CSC = GC.clique_number()
		lista[3] = CSC
	else:
		lista[3] = -1	
	if kLargestDegree == True:
		LD = Degree(G,int(ParamkLargestDegree))
		lista[4] = LD
	else:
		lista[4] = -1
	if kLargestDegreeComplement == True:
		LDC = Degree(G,int(ParamkLargestDegreeComplement))
		lista[5] = LDC
	else:
		lista[5] = -1
	### abertura do arquivo de saída	
	f = open('saida.csv','wb')
	z = csv.writer(f)
	z.writerow(["ChromaticNumber","ChromaticNumberComplement","LargestCliqueSize","LargestCliqueSizeComplement","kLargestDegree","kLargestDegreeComplement"])
	z.writerow(lista)
	f.close()