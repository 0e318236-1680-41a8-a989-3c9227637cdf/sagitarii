import csv

def DegreeModificado(G):
	A = G.adjacency_matrix()
	A = Matrix(A)
	t = A.nrows()
	deg = range(t)
	for i in range(t):
		deg[i] = 0
	for i in range(t):
		for j in range(t):
			deg[i] = deg[i] + A[i,j]
	deg.sort(reverse=True)
	
	DegreeInString = ''
	PipeKey = '|'
	
	for i in range(len(deg)):
		DegStrAux = str(deg[i])
		tam = len(deg)-1
		if i != tam:
			DegreeInString = DegreeInString+DegStrAux+PipeKey
		else:
			DegreeInString = DegreeInString+DegStrAux
		
	#f = open(outputdir + 'degree.csv','wb')
	#f = open('degree.csv','wb')
	#z = csv.writer(f, delimiter = '|')
	#z.writerow(deg)
	#y = csv.writer(f)
	#y.writerow(deg)
	#f.close()
	return deg,DegreeInString