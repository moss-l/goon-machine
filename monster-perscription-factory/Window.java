import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/** Main window for the Monster Prescription Factory program. */
public final class Window extends JFrame {
	private Window() {
		super("Monster Prescription Factory");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setMinimumSize(new java.awt.Dimension(640, 480));
		setLocationRelativeTo(null);

		JPanel background = new JPanel(new BorderLayout(24, 24)) {
			@Override
			protected void paintComponent(Graphics graphics) {
				Graphics2D g = (Graphics2D) graphics.create();
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setPaint(new GradientPaint(0, 0, new Color(18, 24, 45), getWidth(), getHeight(),
						new Color(67, 29, 83)));
				g.fillRect(0, 0, getWidth(), getHeight());
				g.dispose();
			}
		};
		background.setBorder(BorderFactory.createEmptyBorder(48, 56, 48, 56));

		JLabel title = new JLabel("MONSTER PRESCRIPTION FACTORY", SwingConstants.CENTER);
		title.setForeground(new Color(236, 244, 255));
		title.setFont(new Font("SansSerif", Font.BOLD, 30));

		JLabel subtitle = new JLabel("Brew something extraordinary.", SwingConstants.CENTER);
		subtitle.setForeground(new Color(174, 192, 220));
		subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));

		JPanel heading = new JPanel(new BorderLayout(0, 8));
		heading.setOpaque(false);
		heading.add(title, BorderLayout.CENTER);
		heading.add(subtitle, BorderLayout.SOUTH);
		background.add(heading, BorderLayout.NORTH);

		JLabel status = new JLabel("✦  FACTORY ONLINE  ✦", SwingConstants.CENTER);
		status.setForeground(new Color(119, 255, 207));
		status.setFont(new Font("Monospaced", Font.BOLD, 16));
		background.add(status, BorderLayout.CENTER);
		setContentPane(background);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Window().setVisible(true));
	}
}
