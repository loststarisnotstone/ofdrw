import os
import ofdrw
import pdfkit
import pypandoc

# OFD 转 PDF
input_ofd = 'c:\\111.ofd'
output_pdf = 'c:\\output.pdf'

with open(input_ofd, 'rb') as ofd_file:
    document = ofdrw.OFDDocument(ofd_file)
    pdf_data = document.to_pdf()

with open(output_pdf, 'wb') as pdf_file:
    pdf_file.write(pdf_data)

# PDF 转 DOCX
output_docx = 'output.docx'
pypandoc.convert_file(output_pdf, 'docx', outputfile=output_docx)

# 清理临时 PDF 文件
os.remove(output_pdf)

print("转换完成！")