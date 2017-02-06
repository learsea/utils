/**图形操作工具类**/
package com.sibu.putaway.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import goja.QRCode;
import goja.QRCodeFormat;

public class OperateImage {

	/**
	 * 根据url路径获取图片 
	 */
	public static BufferedImage getImgFromUrl(String urlPath) throws IOException {
		HttpGet httpget = new HttpGet(urlPath);
		// 伪装成浏览器
		httpget.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
		CloseableHttpResponse response = Http.httpclient.execute(httpget);
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				try {
					return ImageIO.read(instream);
				} finally {
					instream.close();
				}
			}
			return null;
		} finally {
			response.close();
		}
	}

	/**
	 * 根据图片和图片格式获取输入流
	 */
	public static InputStream getInputStream(BufferedImage bufferedImage, String imageFormat) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = null;
		ImageOutputStream imageOutputStream = null;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
			ImageIO.write(bufferedImage, imageFormat, imageOutputStream);
			return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		} finally {
			if (imageOutputStream != null) {
				imageOutputStream.close();
			}
			if (byteArrayOutputStream != null) {
				byteArrayOutputStream.close();
			}
		}
	}

	public static BufferedImage getQRCodeImage(String content, int size) {
		QRCodeFormat fomart = QRCodeFormat.NEW().setSize(size);
		QRCode qrCode = QRCode.create(content, fomart);
		return qrCode.getQrcodeImage();
	}

	/**
	 * 按倍率缩小图片
	 * @param src 源图片
	 * @param widthRatio 宽度缩小比例
	 * @param heightRatio 高度缩小比例
	 */
	public BufferedImage reduceImageByRatio(BufferedImage src, int widthRatio, int heightRatio) {
		// 读入文件
		int width = src.getWidth();
		int height = src.getHeight();
		// 缩小边长
		BufferedImage tag = new BufferedImage(width / widthRatio, height / heightRatio, BufferedImage.TYPE_INT_RGB);
		// 绘制 缩小 后的图片
		tag.getGraphics().drawImage(src, 0, 0, width / widthRatio, height / heightRatio, null);
		return tag;
	}

	/**
	 * 长高等比例缩小图片
	 * 
	 * @param src 原图片
	 * @param ratio 缩小比例
	 */
	public BufferedImage reduceImageEqualProportion(BufferedImage src, int ratio) {
		int width = src.getWidth();
		int height = src.getHeight();
		// 缩小边长
		BufferedImage tag = new BufferedImage(width / ratio, height / ratio, BufferedImage.TYPE_INT_RGB);
		// 绘制 缩小 后的图片
		tag.getGraphics().drawImage(src, 0, 0, width / ratio, height / ratio, null);
		return tag;
	}

	/**
	 * 按倍率放大图片
	 * @param src 原图形
	 * @param widthRatio 宽度放大比例
	 * @param heightRatio 高度放大比例
	 */
	public BufferedImage enlargementImageByRatio(BufferedImage src, int widthRatio, int heightRatio) {
		int width = src.getWidth();
		int height = src.getHeight();
		// 放大边长
		BufferedImage tag = new BufferedImage(width * widthRatio, height * heightRatio, BufferedImage.TYPE_INT_RGB);
		// 绘制放大后的图片
		tag.getGraphics().drawImage(src, 0, 0, width * widthRatio, height * heightRatio, null);
		return tag;
	}

	/**
	 * 长高等比例放大图片
	 * @param src 原图片
	 * @param ratio 放大比例
	 */
	public BufferedImage enlargementImageEqualProportion(BufferedImage src, int ratio) {
		int width = src.getWidth();
		int height = src.getHeight();
		// 放大边长
		BufferedImage tag = new BufferedImage(width * ratio, height * ratio, BufferedImage.TYPE_INT_RGB);
		// 绘制放大后的图片
		tag.getGraphics().drawImage(src, 0, 0, width * ratio, height * ratio, null);
		return tag;
	}

	/**
	 * 重置图形的边长大小
	 * @param src
	 * @param width
	 * @param height
	 */
	public static BufferedImage resizeImage(BufferedImage src, int width, int height) {
		// 放大边长
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 绘制放大后的图片
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(src, 0, 0, width, height, null);
		g2d.dispose();
		return img;
	}

	/**
	 * 横向拼接图片（两张）
	 * @param imageOne 第一张图片
	 * @param imageTwo 第二张图片
	 */
	public BufferedImage joinImagesHorizontal(BufferedImage imageOne, BufferedImage imageTwo) {
		// 读取第一张图片
		int width = imageOne.getWidth();// 图片宽度
		int height = imageOne.getHeight();// 图片高度
		// 从图片中读取RGB
		int[] imageArrayOne = new int[width * height];
		imageArrayOne = imageOne.getRGB(0, 0, width, height, imageArrayOne, 0, width);

		// 对第二张图片做相同的处理
		int width2 = imageTwo.getWidth();
		int height2 = imageTwo.getHeight();
		int[] ImageArrayTwo = new int[width2 * height2];
		ImageArrayTwo = imageTwo.getRGB(0, 0, width2, height2, ImageArrayTwo, 0, width2);

		// 生成新图片
		int height3 = (height > height2 || height == height2) ? height : height2;
		BufferedImage imageNew = new BufferedImage(width + width2, height3, BufferedImage.TYPE_INT_RGB);
		imageNew.setRGB(0, 0, width, height, imageArrayOne, 0, width);// 设置左半部分的RGB
		imageNew.setRGB(width, 0, width2, height2, ImageArrayTwo, 0, width2);// 设置右半部分的RGB
		return imageNew;
	}

	/**
	 * 横向拼接一组（多张）图像
	 * @param images 将要拼接的图像
	 * @param type 图像写入格式
	 */
	public BufferedImage joinImageListHorizontal(BufferedImage[] images, String type) {
		try {
			int len = images.length;
			if (len < 1) {
				System.out.println("pics len < 1");
				return null;
			}
			int[][] imageArrays = new int[len][];
			for (int i = 0; i < len; i++) {
				int width = images[i].getWidth();
				int height = images[i].getHeight();
				imageArrays[i] = new int[width * height];// 从图片中读取RGB
				imageArrays[i] = images[i].getRGB(0, 0, width, height, imageArrays[i], 0, width);
			}

			int dst_width = 0;
			int dst_height = images[0].getHeight();
			for (int i = 0; i < images.length; i++) {
				dst_height = dst_height > images[i].getHeight() ? dst_height : images[i].getHeight();
				dst_width += images[i].getWidth();
			}
			// System.out.println(dst_width);
			// System.out.println(dst_height);
			if (dst_height < 1) {
				System.out.println("dst_height < 1");
				return null;
			}
			/*
			 * 生成新图片
			 */
			BufferedImage ImageNew = new BufferedImage(dst_width, dst_height, BufferedImage.TYPE_INT_RGB);
			int width_i = 0;
			for (int i = 0; i < images.length; i++) {
				ImageNew.setRGB(width_i, 0, images[i].getWidth(), dst_height, imageArrays[i], 0, images[i].getWidth());
				width_i += images[i].getWidth();
			}
			return ImageNew;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 纵向拼接图片（两张）
	 * @param imageOne 读取的第一张图片
	 * @param imageTwo 读取的第二张图片
	 * @param imageFormat 图片写入格式
	 */
	public BufferedImage joinImagesVertical(BufferedImage imageOne, BufferedImage imageTwo, String imageFormat) {
		int width = imageOne.getWidth();// 图片宽度
		int height = imageOne.getHeight();// 图片高度
		// 从图片中读取RGB
		int[] imageArrayOne = new int[width * height];
		imageArrayOne = imageOne.getRGB(0, 0, width, height, imageArrayOne, 0, width);

		// 对第二张图片做相同的处理
		int width2 = imageTwo.getWidth();
		int height2 = imageTwo.getHeight();
		int[] ImageArrayTwo = new int[width2 * height2];
		ImageArrayTwo = imageTwo.getRGB(0, 0, width2, height2, ImageArrayTwo, 0, width2);

		// 生成新图片
		int width3 = (width > width2 || width == width2) ? width : width2;
		BufferedImage imageNew = new BufferedImage(width3, height + height2, BufferedImage.TYPE_INT_RGB);
		imageNew.setRGB(0, 0, width, height, imageArrayOne, 0, width);// 设置上半部分的RGB
		imageNew.setRGB(0, height, width2, height2, ImageArrayTwo, 0, width2);// 设置下半部分的RGB
		return imageNew;
	}

	/**
	 * 纵向拼接一组（多张）图像
	 * @param images 将要拼接的图像数组
	 * @param type 写入图像类型
	 */
	public BufferedImage joinImageListVertical(BufferedImage[] images, String type) {
		try {
			int len = images.length;
			if (len < 1) {
				System.out.println("pics len < 1");
				return null;
			}
			int[][] imageArrays = new int[len][];
			for (int i = 0; i < len; i++) {
				// System.out.println(i);
				int width = images[i].getWidth();
				int height = images[i].getHeight();
				imageArrays[i] = new int[width * height];// 从图片中读取RGB
				imageArrays[i] = images[i].getRGB(0, 0, width, height, imageArrays[i], 0, width);
			}

			int dst_height = 0;
			int dst_width = images[0].getWidth();
			for (int i = 0; i < images.length; i++) {
				dst_width = dst_width > images[i].getWidth() ? dst_width : images[i].getWidth();
				dst_height += images[i].getHeight();
			}
			// System.out.println(dst_width);
			// System.out.println(dst_height);
			if (dst_height < 1) {
				System.out.println("dst_height < 1");
				return null;
			}
			/*
			 * 生成新图片
			 */
			BufferedImage ImageNew = new BufferedImage(dst_width, dst_height, BufferedImage.TYPE_INT_RGB);
			int height_i = 0;
			for (int i = 0; i < images.length; i++) {
				ImageNew.setRGB(0, height_i, dst_width, images[i].getHeight(), imageArrays[i], 0, dst_width);
				height_i += images[i].getHeight();
			}
			return ImageNew;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 合并图片(按指定初始x、y坐标将附加图片贴到底图之上)
	 * 
	 * @param negativeImage 背景图片路径
	 * @param additionImage 附加图片
	 * @param x 附加图片的起始点x坐标
	 * @param y 附加图片的起始点y坐标
	 */
	public static BufferedImage mergeBothImage(BufferedImage negativeImage, BufferedImage additionImage, int x, int y) {
		Graphics2D g2d = negativeImage.createGraphics();
		g2d.drawImage(additionImage, x, y, null);
		g2d.dispose();
		return negativeImage;
	}

	/**
	 * 将附加图片合并到底图的左上角
	 * @param negativeImage 底图
	 * @param additionImage 附加图片
	 */
	public BufferedImage mergeBothImageTopleftcorner(BufferedImage negativeImage, BufferedImage additionImage) {
		Graphics2D g2d = negativeImage.createGraphics();
		g2d.drawImage(additionImage, 0, 0, null);
		g2d.dispose();
		return negativeImage;
	}

	/**
	 * 将附加图片合并到底图的右上角
	 * @param negativeImage 底图
	 * @param additionImage 附加图片
	 */
	public BufferedImage mergeBothImageToprightcorner(BufferedImage negativeImage, BufferedImage additionImage) {
		Graphics2D g2d = negativeImage.createGraphics();
		g2d.drawImage(additionImage, negativeImage.getWidth() - additionImage.getWidth(), 0, null);
		g2d.dispose();
		return negativeImage;
	}

	/**
	 * 将附加图片合并到底图的左下角
	 * @param negativeImage 底图
	 * @param additionImage 附加图片
	 */
	public BufferedImage mergeBothImageLeftbottom(BufferedImage negativeImage, BufferedImage additionImage) {
		Graphics2D g2d = negativeImage.createGraphics();
		g2d.drawImage(additionImage, 0, negativeImage.getHeight() - additionImage.getHeight(), null);
		g2d.dispose();
		return negativeImage;
	}

	/**
	 * 将附加图片合并到底图的左下角
	 * @param negativeImage 底图
	 * @param additionImage 附加图片
	 */
	public BufferedImage mergeBothImageRightbottom(BufferedImage negativeImage, BufferedImage additionImage) {
		Graphics2D g2d = negativeImage.createGraphics();
		g2d.drawImage(additionImage, negativeImage.getWidth() - additionImage.getWidth(),
				negativeImage.getHeight() - additionImage.getHeight(), null);
		g2d.dispose();
		return negativeImage;
	}

	/**
	 * 将附加图片合并到底图的正中央
	 * @param negativeImage 底图
	 * @param additionImage 附加图片
	 */
	public static BufferedImage mergeBothImageCenter(BufferedImage negativeImage, BufferedImage additionImage) {
		Graphics2D g2d = negativeImage.createGraphics();
		g2d.drawImage(additionImage, negativeImage.getWidth() / 2 - additionImage.getWidth() / 2,
				negativeImage.getHeight() / 2 - additionImage.getHeight() / 2, null);
		g2d.dispose();
		return negativeImage;
	}

	/**
	 * 将附加图片合并到底图的上边中央
	 * @param negativeImage 底图
	 * @param additionImage 附加图片
	 */
	public BufferedImage mergeBothImageTopcenter(BufferedImage negativeImage, BufferedImage additionImage) {
		Graphics2D g2d = negativeImage.createGraphics();
		g2d.drawImage(additionImage, negativeImage.getWidth() / 2 - additionImage.getWidth() / 2, 0, null);
		g2d.dispose();
		return negativeImage;
	}

	/**
	 * 将附加图片合并到底图的下边中央
	 * @param negativeImage 底图
	 * @param additionImage 附加图片
	 */
	public BufferedImage mergeBothImageBottomcenter(BufferedImage negativeImag, BufferedImage additionImage) {
		Graphics2D g2d = negativeImag.createGraphics();
		g2d.drawImage(additionImage, negativeImag.getWidth() / 2 - additionImage.getWidth() / 2,
				negativeImag.getHeight() - additionImage.getHeight(), null);
		g2d.dispose();
		return negativeImag;
	}

	/**
	 * 将附加图片合并到底图的左边中央
	 * @param negativeImage 底图
	 * @param additionImage 附加图片
	 */
	public BufferedImage mergeBothImageLeftcenter(BufferedImage negativeImag, BufferedImage additionImage) {
		Graphics2D g2d = negativeImag.createGraphics();
		g2d.drawImage(additionImage, 0, negativeImag.getHeight() / 2 - additionImage.getHeight() / 2, null);
		g2d.dispose();
		return negativeImag;
	}

	/**
	 * 将附加图片合并到底图的右边中央
	 * @param negativeImage 底图
	 * @param additionImage 附加图
	 */
	public BufferedImage mergeBothImageRightcenter(BufferedImage negativeImag, BufferedImage additionImage) {
		Graphics2D g2d = negativeImag.createGraphics();
		g2d.drawImage(additionImage, negativeImag.getWidth() - additionImage.getWidth(),
				negativeImag.getHeight() / 2 - additionImage.getHeight() / 2, null);
		g2d.dispose();
		return negativeImag;
	}

	/**
	 * 图片灰化操作
	 * @param image 源图片
	 */
	public BufferedImage grayImage(BufferedImage image) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		image = op.filter(image, null);
		return image;
	}

	/**
	 * 在源图片上设置水印文字
	 * @param image 源图片路径
	 * @param alpha 透明度（0<alpha<1）
	 * @param font 字体（例如：宋体）
	 * @param fontStyle 字体格式(例如：普通样式--Font.PLAIN、粗体--Font.BOLD )
	 * @param fontSize 字体大小
	 * @param color 字体颜色(例如：黑色--Color.BLACK)
	 * @param inputWords 输入显示在图片上的文字
	 * @param x 文字显示起始的x坐标
	 * @param y 文字显示起始的y坐标
	 */
	public static BufferedImage alphaWords2Image(BufferedImage image, float alpha, String font, int fontStyle,
			int fontSize, Color color, String inputWords, int x, int y) {
		// 创建java2D对象
		Graphics2D g2d = image.createGraphics();
		// 用源图像填充背景
		g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null, null);
		// 设置透明度
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2d.setComposite(ac);
		// 设置文字字体名称、样式、大小
		g2d.setFont(new Font(font, fontStyle, fontSize));
		g2d.setColor(color);// 设置字体颜色
		g2d.drawString(inputWords, x, y); // 输入水印文字及其起始x、y坐标
		g2d.dispose();
		return image;
	}

	/**
	 * 在源图像上设置图片水印 ---- 当alpha==1时文字不透明（和在图片上直接输入文字效果一样）
	 * @param image 源图片路径
	 * @param alpha 透明度
	 * @param x 水印图片的起始x坐标
	 * @param y 水印图片的起始y坐标
	 * @param width 水印图片的宽度
	 * @param height 水印图片的高度
	 */
	public BufferedImage alphaImage2Image(BufferedImage image, BufferedImage appendImage, float alpha, int x, int y,
			int width, int height) {
		// 创建java2D对象
		Graphics2D g2d = image.createGraphics();
		// 用源图像填充背景
		g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null, null);
		// 设置透明度
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2d.setComposite(ac);
		// 设置水印图片的起始x/y坐标、宽度、高度
		g2d.drawImage(appendImage, x, y, width, height, null, null);
		g2d.dispose();
		return image;
	}

	/**
	 * 画单点 ---- 实际上是画一个填充颜色的圆 ---- 以指定点坐标为中心画一个小半径的圆形，并填充其颜色来充当点
	 * @param image 源图片
	 * @param x 点的x坐标
	 * @param y 点的y坐标
	 * @param width 填充的宽度
	 * @param height 填充的高度
	 * @param ovalColor 填充颜色
	 */
	public BufferedImage drawPoint(BufferedImage image, int x, int y, int width, int height, Color ovalColor) {
		// 根据xy点坐标绘制连接线
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(ovalColor);
		// 填充一个椭圆形
		g2d.fillOval(x, y, width, height);
		g2d.dispose();
		return image;
	}

	/**
	 * 画一组（多个）点---- 实际上是画一组（多个）填充颜色的圆 ---- 以指定点坐标为中心画一个小半径的圆形，并填充其颜色来充当点
	 * @param image 原图片
	 * @param pointList 点列表
	 * @param width 宽度
	 * @param height 高度
	 * @param ovalColor 填充颜色
	 */
	public BufferedImage drawPoints(BufferedImage image, List<Point> pointList, int width, int height,
			Color ovalColor) {
		// 根据xy点坐标绘制连接线
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(ovalColor);
		// 填充一个椭圆形
		if (pointList != null) {
			for (int i = 0; i < pointList.size(); i++) {
				Point point = pointList.get(i);
				int x = (int) point.getX();
				int y = (int) point.getY();
				g2d.fillOval(x, y, width, height);
			}
		}
		g2d.dispose();
		return image;
	}

	/**
	 * 画线段
	 * @param image 源图片
	 * @param x1 第一个点x坐标
	 * @param y1 第一个点y坐标
	 * @param x2 第二个点x坐标
	 * @param y2 第二个点y坐标
	 * @param lineColor 线条颜色
	 */
	public BufferedImage drawLine(BufferedImage image, int x1, int y1, int x2, int y2, Color lineColor) {
		// 根据xy点坐标绘制连接线
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(lineColor);
		g2d.drawLine(x1, y1, x2, y2);
		g2d.dispose();
		return image;
	}

	/**
	 * 画折线 / 线段 ---- 2个点即画线段，多个点画折线
	 * @param image 源图片
	 * @param xPoints x坐标数组
	 * @param yPoints y坐标数组
	 * @param nPoints 点的数量
	 * @param lineColor 线条颜色
	 */
	public BufferedImage drawPolyline(BufferedImage image, int[] xPoints, int[] yPoints, int nPoints, Color lineColor) {
		// 根据xy点坐标绘制连接线
		Graphics2D g2d = image.createGraphics();
		// 设置线条颜色
		g2d.setColor(lineColor);
		g2d.drawPolyline(xPoints, yPoints, nPoints);
		g2d.dispose();
		return image;
	}

	/**
	 * 绘制折线，并突出显示转折点
	 * @param image 源图片
	 * @param xPoints x坐标数组
	 * @param yPoints y坐标数组
	 * @param nPoints 点的数量
	 * @param lineColor 连线颜色
	 * @param width 点的宽度
	 * @param height 点的高度
	 * @param ovalColor 点的填充颜色
	 */
	public BufferedImage drawPolylineShowPoints(BufferedImage image, int[] xPoints, int[] yPoints, int nPoints,
			Color lineColor, int width, int height, Color ovalColor) {
		// 根据xy点坐标绘制连接线
		Graphics2D g2d = image.createGraphics();
		// 设置线条颜色
		g2d.setColor(lineColor);
		// 画线条
		g2d.drawPolyline(xPoints, yPoints, nPoints);
		// 设置圆点颜色
		g2d.setColor(ovalColor);
		// 画圆点
		if (xPoints != null) {
			for (int i = 0; i < xPoints.length; i++) {
				int x = xPoints[i];
				int y = yPoints[i];
				g2d.fillOval(x, y, width, height);
			}
		}
		g2d.dispose();
		return image;
	}

	/**
	 * 绘制一个由 x 和 y 坐标数组定义的闭合多边形
	 * @param image 源图片
	 * @param xPoints x坐标数组
	 * @param yPoints y坐标数组
	 * @param nPoints 坐标点的个数
	 * @param polygonColor 线条颜色
	 */
	public BufferedImage drawPolygon(BufferedImage image, int[] xPoints, int[] yPoints, int nPoints,
			Color polygonColor) {
		// 根据xy点坐标绘制闭合多边形
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(polygonColor);
		g2d.drawPolygon(xPoints, yPoints, nPoints);
		g2d.dispose();
		return image;
	}

	/**
	 * 绘制并填充多边形
	 * @param image 源图像
	 * @param xPoints x坐标数组
	 * @param yPoints y坐标数组
	 * @param nPoints 坐标点个数
	 * @param polygonColor 多边形填充颜色
	 * @param alpha 多边形部分透明度
	 */
	public BufferedImage drawAndAlphaPolygon(BufferedImage image, int[] xPoints, int[] yPoints, int nPoints,
			Color polygonColor, float alpha) {
		// 根据xy点坐标绘制闭合多边形
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(polygonColor);
		// 设置透明度
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2d.setComposite(ac);
		g2d.fillPolygon(xPoints, yPoints, nPoints);
		g2d.dispose();
		return image;
	}
}
