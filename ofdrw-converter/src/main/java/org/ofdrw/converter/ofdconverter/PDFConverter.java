package org.ofdrw.converter.ofdconverter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.ofdrw.converter.GeneralConvertException;
import org.ofdrw.graphics2d.OFDGraphicsDocument;
import org.ofdrw.graphics2d.OFDPageGraphics2D;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * PDF转换为OFD转换器
 *
 * @author 权观宇
 * @since 2023-3-14 23:09:08
 */
public class PDFConverter implements DocConverter {

    /**
     * 是否已经关闭
     */
    private boolean closed = false;


    /**
     * OFD图形文档对象
     */
    final OFDGraphicsDocument ofdDoc;

    /**
     * PDF坐标系 转换 OFD坐标系 缩放比例
     * PDF user unit / OFD mm
     * user unit per millimeter
     */
    final double UUPMM = 2.8346;

    /**
     * 创建PDF转换OFD转换器
     *
     * @param ofdPath 转换后的OFD文件路径
     * @throws IOException 文件解析异常
     */
    public PDFConverter(Path ofdPath) throws IOException {
        if (ofdPath == null) {
            throw new IllegalArgumentException("转换后的OFD文件路径为空");
        }

        ofdPath = ofdPath.toAbsolutePath();
        if (!Files.exists(ofdPath)) {
            Path parent = ofdPath.getParent();
            if (Files.exists(parent)) {
                if (!Files.isDirectory(parent)) {
                    throw new IllegalArgumentException("已经存在同名文件: " + parent);
                }
            } else {
                Files.createDirectories(parent);
            }
            Files.createFile(ofdPath);
        }
        ofdDoc = new OFDGraphicsDocument(ofdPath);
    }


    /**
     * PDF转换为OFD页面
     *
     * @param filepath 待转换文件路径
     * @param indexes  【可选】【可变】待转换页码（从0起），该参数仅在转换源文件类型为类文档文件时有效，当该参数不传或为空时表示转换全部内容到OFD。
     * @throws GeneralConvertException 转换异常
     */
    @Override
    public void convert(Path filepath, int... indexes) throws GeneralConvertException {
        if (filepath == null || !Files.exists(filepath) || Files.isDirectory(filepath)) {
            return;
        }

        try (PDDocument pdfDoc = PDDocument.load(filepath.toFile())) {
            int total = pdfDoc.getNumberOfPages();
            List<Integer> targetPages = new LinkedList<>();
            if (indexes == null || indexes.length == 0) {
                for (int i = 0; i < total; i++) {
                    targetPages.add(i);
                }
            } else {
                // 获取指定页面信息
                for (int index : indexes) {
                    if (index < 0 || index >= total) {
                        continue;
                    }
                    targetPages.add(index);
                }
            }

            PDFRenderer pdfRender = new PDFRenderer(pdfDoc);
            RenderingHints r = new RenderingHints(null);
            r.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            // 设置渲染模式为快速，关闭PDFBox对图片的压缩
            r.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            r.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pdfRender.setRenderingHints(r);

            for (Integer index : targetPages) {
                PDRectangle pdfPageSize = pdfDoc.getPage(index).getBBox();
                // 将PDF页面尺寸缩放至OFD尺寸
                OFDPageGraphics2D ofdPageG2d = ofdDoc.newPage(pdfPageSize.getWidth() / UUPMM, pdfPageSize.getHeight() / UUPMM);
                pdfRender.renderPageToGraphics(index, ofdPageG2d, (float) (1d / UUPMM));
            }
        } catch (IOException e) {
            throw new GeneralConvertException("PDF转换OFD异常", e);
        }
    }


    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        closed = true;
        if (ofdDoc != null) {
            ofdDoc.close();
        }
    }
}
