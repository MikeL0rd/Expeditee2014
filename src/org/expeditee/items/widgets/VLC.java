package org.expeditee.items.widgets;

import java.io.File;

import javax.swing.JPanel;
import javafx.application.Platform;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

import org.expeditee.items.Text;

public class VLC extends InteractiveWidget {

	protected EmbeddedMediaPlayerComponent _mediaPlayer;
	protected JPanel _panel;
	protected String _media;

	public VLC(Text source, String[] args) {
		super(source, new JPanel(), 1280, 1280, 720, 720);

		_panel = (JPanel) _swingComponent;
		_media = (args != null && args.length > 0) ? args[0] : "";
		System.out.println(_media);

		initPlayer();
	}

	@Override
	protected String[] getArgs() {
		return new String[] { this._media };
	}

	private void initPlayer() {
		//Find libvlc path
		new NativeDiscovery().discover();

		_mediaPlayer = new EmbeddedMediaPlayerComponent();
//		_panel.setContentPane(_mediaPlayer);
		_panel.add(_mediaPlayer);
		_panel.setSize(1280, 720);
		_panel.setVisible(true);
	
		File f = new File(_media);
		String media = new String(f.toURI().toString());
		System.out.println(media);

		_mediaPlayer.getMediaPlayer().playMedia(media);
	}
}
