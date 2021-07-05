import fitz
import re

def flags_decomposer(flags):
	l = []
	if flags & 2 ** 0:
		l.append("superscript")
	if flags & 2 ** 1:
		l.append("italic")
	if flags & 2 ** 2:
		l.append("serifed")
	else:
		l.append("sans")
	if flags & 2 ** 3:
		l.append("monospaced")
	else:
		l.append("proportional")
	if flags & 2 ** 4:
		l.append("bold")
	return l



class Texto():

	def __init__(self,information,text_type,sheets = [0,-1]):
		self.information = []
		self.otra_informacion = []
		if text_type == 1:
			file = fitz.open(information)
			if sheets[1] == -1:
				sheets[1] = file.pageCount
			for i in range(sheets[0],sheets[1]):
				hoja = self.get_sheetData(file,i)
				blue = self.get_blue_section(file,i)
				aux = self.clean(hoja)
				
				if self.approve_text(aux):
					self.information.append(aux)

				self.otra_informacion = blue

			file.close()
		elif text_type == 0:
			self.information = [self.clean(information)]
		self.segments = []
		for i in range(0,len(self.information)):
			print("-"*40)
			print("Pagina " + str(i))
			self.segments.append(self.make_list(self.information[i]))

	def get_sheetData(self,file, sheet):
		page = file.loadPage(sheet)
		text = page.getText("text")
		return text

	def get_blue_section(self,file,sheet):
		bandera = True
		texto = []
		aux = ""
		caracteristicas = []
		page = file.loadPage(sheet)
		blocks = page.get_text("dict", flags = 11)["blocks"]
		for b in blocks:
			for l in b["lines"]:
				for s in l["spans"]:
					caracteristicas = flags_decomposer(s["flags"])
					caracteristicas.append(s["font"])
					color = "#%06x" % (s["color"])
					caracteristicas.append(color)

				#################################
				######    CONDICION   ###########
				#################################
				#################################

				if "italic" in caracteristicas and "#1f4e79" in caracteristicas: ## Condicion a modificar
					aux = aux + s["text"]
					bandera = False
				else:
					bandera = True
				if bandera == True:
					texto.append(aux)
					aux = ""

		bandera = True
		while bandera == True:
			try:
				texto.remove("")
			except:
				bandera = False
		return texto

	def clean(self,text):
		# Eliminar separadores
		new_text = re.sub(r"__+", ' ', text)
		new_text = re.sub(r"--+", ' ', new_text)
		
		#Eliminar encabezados, pies de pagina y numero
		i = 0
		jump = 0
		
		while ord(new_text[i]) < 49 or ord(new_text[i]) > 57:
			if new_text[i] == "\n":
				jump += 1
				if jump >= 2:
					#new_text = "\n" + new_text[:]
					i = 0
					break
			i += 1

		new_text = new_text[i:]
		new_text = new_text[new_text.index("\n") + 1:]

		#Eliminar informacion de contacto, fechas y horas
		new_text = re.sub(r"[0-9]+:[0-9]+:[0-9]+ ?", ' ', new_text)
		new_text = re.sub(r"[0-9]+/[0-9]+/[0-9]+ ?", ' ', new_text)
		new_text = re.sub(r"[0-9]*-[0-9-]*-?", ' ', new_text)
		new_text = re.sub(r"[a-zA-Z0-9_]+[a-zA-Z0-9_.]*@[a-zA-Z]+.[a-zA-Z]+[.a-zA-Z]*", ' ', new_text)
		new_text = re.sub(r"https?://[A-Za-z0-9./]+", ' ', new_text)
		new_text = re.sub(r"www.[A-Za-z0-9./]+", ' ', new_text)

		
		#Eliminar posibles indices
		new_text = re.sub(r"[a-zA-Z0-9.,;: áéíóúÁÉÍÓÚ \n ñÑ¿?¡!]+[.]{4,} ?[0-9]{1,3}", ' ', new_text)
		
		#Eliminar simbolos innecesarios
		#new_text = re.sub(r'[^0-9a-zA-Z.¡!¿?_(),\náéíóú \-"]', ' ', new_text)

		#Eliminar espacios y saltos de linea de mas
		#new_text = re.sub(r"\n+", ' ', new_text)
		new_text = re.sub(r"\t+", ' ', new_text)
		new_text = re.sub(r" +", ' ', new_text)
		return new_text

	def approve_text(self,text):
		new_text = re.sub(r"\n+", ' ', text)
		new_text = re.sub(r" +", ' ', new_text)
		if len(new_text) > 200:
			return True
		else:
			return False
		
	def make_list(self,text):
		aux = text.split(".")
		final = []
		for i in range(0,len(aux)):
			aux[i] = re.sub(r"\t+", ' ', aux[i])
			aux[i] = re.sub(r"\n+", ' ', aux[i])
			aux[i] = re.sub(r" +", ' ', aux[i])
			if aux[i].count(" ") > 2:
				final.append(aux[i])
		return final

	def get_information(self):
		return self.information

	def get_other_information(self):
		return self.otra_informacion

	def get_segments(self):
		return self.segments


libro = 'Titulo.pdf'# 'Texto.pdf'

pagina = int(input("Pagina: "))
texto = Texto(libro,1,[0,pagina])
print(texto.get_other_information())
