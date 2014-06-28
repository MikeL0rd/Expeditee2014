package org.expeditee.items.widgets;

import java.io.File;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.test.basic.PlayerControlsPanel;
import uk.co.caprica.vlcj.test.basic.PlayerVideoAdjustPanel;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import org.expeditee.items.Text;

public class VLCFrame extends InteractiveWidget {

	private JPanel panel;

	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer mediaPlayer;
	private Canvas videoSurf;
	private PlayerControlsPanel controlPanel;
	private PlayerVideoAdjustPanel adjustPanel;
	private String media;

	public VLCFrame(Text source, String[] args) {
		super(source, new JPanel(), 480, -1, 360, -1);

		panel = (JPanel) _swingComponent;
		media = (args != null && args.length > 0) ? args[0] : "";
		System.out.println(media);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initPlayer();
			}
		});
	}

	/* This will get the video URL from arguments.
	 * It can either be an http URL for sites such as youtube,
	 * or can point to a file on your computer.
	 */
	@Override
	protected String[] getArgs() {
		return new String[] { this.media };
	}

	private void initPlayer() {
		// Loading Native VLC libraries for Windows
		System.loadLibrary("jawt");
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),
				"C:/Program Files/VideoLAN/VLC/");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		// Creating VLC media components, which are used used to create a VLC instance
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaPlayer = mediaPlayerComponent.getMediaPlayer();

		// Setting our video surface to the VLC media components
		videoSurf = mediaPlayerComponent.getVideoSurface();
		videoSurf.setBackground(Color.black);

		// Setting arguments to pass to VLC
		ArrayList<String> vlcArgs = new ArrayList<String>();
		vlcArgs.add("--no-plugins-cache");
		vlcArgs.add("--no-video-title-show");
		vlcArgs.add("--no-snapshot-preview");

		// Creating the video player itself
		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
		mediaPlayerFactory.setUserAgent("vlcj test player");
		mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoSurf));
		mediaPlayer.setPlaySubItems(true);

		// Creating the controls that are used for the video
		controlPanel = new PlayerControlsPanel(mediaPlayer);
		adjustPanel = new PlayerVideoAdjustPanel(mediaPlayer);

		// Packing video surface and controls into a JFrame
		panel.setLayout(new BorderLayout());
		panel.add(mediaPlayerComponent.getVideoSurface(), BorderLayout.CENTER);
		panel.add(controlPanel, BorderLayout.SOUTH);
		panel.add(adjustPanel, BorderLayout.EAST);
		panel.setVisible(true);

		// Start the video
		if (media != null) {
			mediaPlayer.playMedia(media);
		}
	}
}
