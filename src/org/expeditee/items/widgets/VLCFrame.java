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

	private static EmbeddedMediaPlayerComponent _mediaPlayerComponent;
	private static EmbeddedMediaPlayer _mediaPlayer;
	private static Canvas _videoSurf;
	private static PlayerControlsPanel _controlPanel;
	private static PlayerVideoAdjustPanel _adjustPanel;
	private static String _media;

	public static boolean running = false;

	public VLCFrame(Text source, String[] args) {
		super(source, new JPanel(), 480, -1, 360, -1);

		panel = (JPanel) _swingComponent;
		_media = (args != null && args.length > 0) ? args[0] : "";
		System.out.println(_media);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initPlayer();
			}
		});
	}

	@Override
	protected String[] getArgs() {
		return new String[] { this._media };
	}

	private void initPlayer() {
		if (running == false) {
			// Loading VLC libraries
			System.loadLibrary("jawt");
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:/Program Files/VideoLAN/VLC/");
			Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

			System.out.println("No previous VLC instance found");

			_mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
			_mediaPlayer = _mediaPlayerComponent.getMediaPlayer();

			_videoSurf = _mediaPlayerComponent.getVideoSurface();
			_videoSurf.setBackground(Color.black);

			ArrayList<String> vlcArgs = new ArrayList<String>();
			vlcArgs.add("--no-plugins-cache");
			vlcArgs.add("--no-video-title-show");
			vlcArgs.add("--no-snapshot-preview");

			MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
			mediaPlayerFactory.setUserAgent("vlcj test player");
			_mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(_videoSurf));
			_mediaPlayer.setPlaySubItems(true);

			_controlPanel = new PlayerControlsPanel(_mediaPlayer);
			_adjustPanel = new PlayerVideoAdjustPanel(_mediaPlayer);
		}

		panel.setLayout(new BorderLayout());
		panel.add(_mediaPlayerComponent.getVideoSurface(), BorderLayout.CENTER);
		panel.add(_controlPanel, BorderLayout.SOUTH);
		panel.add(_adjustPanel, BorderLayout.EAST);
		panel.setVisible(true);

		if (running == true) {
			_mediaPlayer.pause();
			_mediaPlayer.play();
		}
		if (running == false && _media != null) {
			_mediaPlayer.playMedia(_media);
			running = true;
		}
	}
}
