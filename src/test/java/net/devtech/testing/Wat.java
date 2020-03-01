package net.devtech.testing;

import net.devtech.yajslib.PrimitiveContainer;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Wat {
	private static final int WAVELENGTH = 32;

	// range -16 to 16
	private static final int[] SINE = new int[WAVELENGTH * 2];

	static {
		for (int i = 0; i < SINE.length; i++) {
			SINE[i] = (int) (Math.sin(i * Math.PI / WAVELENGTH) * 16);
		}
	}

	private static final int BLACK = Color.WHITE.getRGB();
	public static void main(String[] args) throws IOException {
		int len = 1024;
		BufferedImage image = new BufferedImage(len, 256, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < len; i++) {
			image.setRGB(i, curve(sample(i)), BLACK);
		}
		File temp = File.createTempFile("test", ".png");
		ImageIO.write(image, "png", temp);
		Desktop.getDesktop().open(temp);
	}

	private static int sample(int i) {
		return SINE[i / 2] + SINE[i / 3] + SINE[i / 5] + SINE[i / 7] + SINE[i / 11] + SINE[i / 13] + SINE[i / 17];
	}

	private static int curve(int i) {
		if (i < 0) {
			return (-16 * i - 1) / (-i + 1) + 1;
		} else if (i > 0) {
			return (16 * i - 1) / (i + 1) + 1;
		}
		return 0;
	}
}
