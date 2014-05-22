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

	protected EmbeddedMediaPlayerComponent _mediaPlayer;
	protected JPanel _panel;
	protected String _media;

	public VLCFrame(Text source, String[] args) {
		super(source, new JPanel(), 480, -1, 360, -1);

		_panel = (JPanel) _swingComponent;
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
		System.loadLibrary("jawt");
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:/Program Files/VideoLAN/VLC/");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		EmbeddedMediaPlayer embeddedMediaPlayer = mediaPlayerComponent.getMediaPlayer();

		Canvas videoSurface = new Canvas();
		videoSurface.setBackground(Color.black);
		videoSurface.setSize(800, 600);

		ArrayList<String> vlcArgs = new ArrayList<String>();
		vlcArgs.add("--no-plugins-cache");
		vlcArgs.add("--no-video-title-show");
		vlcArgs.add("--no-snapshot-preview");

		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
		mediaPlayerFactory.setUserAgent("vlcj test player");
		embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoSurface));
		embeddedMediaPlayer.setPlaySubItems(true);

		final PlayerControlsPanel controlsPanel = new PlayerControlsPanel(embeddedMediaPlayer);
		PlayerVideoAdjustPanel videoAdjustPanel = new PlayerVideoAdjustPanel(embeddedMediaPlayer);

		_panel.setLayout(new BorderLayout());
		_panel.add(videoSurface, BorderLayout.CENTER);
		_panel.add(controlsPanel, BorderLayout.SOUTH);
		_panel.add(videoAdjustPanel, BorderLayout.EAST);

		_panel.setVisible(true);
		if(_media != null)
			embeddedMediaPlayer.playMedia(_media);
	}
}
