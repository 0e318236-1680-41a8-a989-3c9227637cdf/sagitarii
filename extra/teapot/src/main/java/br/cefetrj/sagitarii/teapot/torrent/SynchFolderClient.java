package br.cefetrj.sagitarii.teapot.torrent;

import java.io.File;
import java.io.FileOutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.Client.ClientState;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.client.peer.SharingPeer;
import com.turn.ttorrent.common.Torrent;


// http://www.tools4noobs.com/online_tools/torrent_decode/
// https://github.com/fujohnwang/fujohnwang.github.com/blob/master/posts/2012-04-09-playing-bittorrent-with-ttorrent.md
// https://github.com/mpetazzoni/ttorrent

public class SynchFolderClient {
	private String serverRootFolder;
	private String storageFolder;
	private InetAddress bindAddress;
	private URI trackerAnnounceUrl;
	private boolean canCreateTorrent = false;
	private Client client;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private boolean canClose = false;
	private int ticks = 0;
	private int tooFewCounter = 0;
	private String torrentFile;
	
	public String getTorrentFile() {
		return torrentFile;
	}
	
	public Client getClient() {
		return client; 
	}

	private void sleep( long time ) {
		try {
			Thread.sleep( time );
		} catch (Exception e) {
			//
		}
	}
	
	public void waiForFinish() throws Exception  {
		while ( !canClose ) {
			show();
			sleep(2000);
		}
		logger.debug("Torrent client " + client.getTorrent().getCreatedBy() + " released.");
		stop();
	}
	
	private void show() throws Exception {
		canClose = false;
		ClientState state = client.getState();
		ticks ++;
		
		if( state == ClientState.VALIDATING ) {
			double completion = client.getTorrent().getCompletion();
			logger.debug( client.getTorrent().getCreatedBy() + ": validating " + completion + "% (" + ticks + ")" );
			
			if( (ticks > 20) && ( completion == 100 ) ) {
				logger.error("restart sharing of " + client.getTorrent().getCreatedBy()  + " due to valitation error.");
				ticks = 0;
				client.stop();
				shareFile( torrentFile );
			}
			
			return;
		}

		boolean isInterested = false; boolean isConnected = false; int totalValidPeers = 0;
		String ii = " "; String cc = " ";
		for ( SharingPeer sp : client.getPeers() ) {
			if ( sp.getHexPeerId() != null ) {
				totalValidPeers++;
				if ( sp.isInterested()  ) {
					isInterested = true;
					ii = "I";
				}
				if ( sp.isConnected() ) {
					isConnected = true;
					cc = "C";
				}
			}
		}
		
		
		if( (ticks > 50) && ( !isConnected && !isInterested ) ) {
			logger.error("restart sharing of " + client.getTorrent().getCreatedBy()  + " due to client freezing.");
			ticks = 0;
			client.stop();
			shareFile( torrentFile );
			return;
		}
		
		if ( totalValidPeers == 0 ) {
			logger.debug( client.getTorrent().getCreatedBy() + ": too few peers. Will wait for more...");
			tooFewCounter++;
			if ( tooFewCounter > 20 ) {
				canClose = true;
			}
			return;
		}

		try {
			
			canClose = true;
			if ( isConnected || isInterested ) {
				canClose = false;
			}
			
			
			// Just to show... can be removed.
			logger.debug(client.getTorrent().getCreatedBy() + ": " + totalValidPeers + " peers. (" + 
					canClose + " / " + ticks + ") " + state + " [" + cc + "|" + ii + "] ");
			
			
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
		}
	}
	
	public void stop() {
		client.stop();
		logger.debug("client " + client.getTorrent().getCreatedBy() + " finished.");
	}
	
	private void config( String rootFolder, InetAddress bindAddress ) throws Exception {
		this.serverRootFolder = rootFolder;
		this.bindAddress = bindAddress;
		this.storageFolder = serverRootFolder;

		try {
			new File( storageFolder ).mkdirs();
		} catch ( Exception e ) { 
			//
		}
	}
	
	public SynchFolderClient( String rootFolder, InetAddress bindAddress, String announceUrl ) throws Exception {
		setTrackerUrl( announceUrl );
		config( rootFolder, bindAddress );
	}
	
	
	public SynchFolderClient( String rootFolder, String announceUrl ) throws Exception {
		setTrackerUrl( announceUrl );
		config( rootFolder, getFirstNonLoopbackAddress(true,false) );
	}
	
	private List<File> getFiles( String folderName ) {
		File folder = new File( folderName );
		List<File> files = new ArrayList<File>();
		for (File fileEntry : folder.listFiles() ) {
	        if ( !fileEntry.isDirectory() ) {
	        	files.add( fileEntry );
	        } else {
	        	files.addAll( getFiles( fileEntry.getAbsolutePath() ) );
	        }
	    }
		return files;
	}

	public List<String> getTorrentFiles() {
		File folder = new File( serverRootFolder );
		List<String> files = new ArrayList<String>();
		for (File fileEntry : folder.listFiles() ) {
	        if ( !fileEntry.isDirectory() ) {
	        	files.add( serverRootFolder + "/" + fileEntry.getName() );
	        } 
	    }
		return files;
	}
	
	public Torrent createTorrentFromFolder( String folderPath, String folderName ) throws Exception {
		logger.debug("creating outbox torrent from " + folderPath );
		if ( !canCreateTorrent ) throw new Exception("Cannot create torrent without Announce URL");
		String sourceFolder = storageFolder + "/" + folderPath + "/" + folderName;
		sourceFolder = sourceFolder.replaceAll("/+", "/");

		logger.debug("full path: " + sourceFolder);
		
		File ff = new File(sourceFolder);
		List<File> fileList = getFiles( sourceFolder );
		if ( fileList.size() > 0 ) {
			Torrent torrent = Torrent.create(
					ff, 
					fileList, 
					trackerAnnounceUrl, folderPath);
			
			String torrentFile = storageFolder + "/" + folderPath + "/" + torrent.getHexInfoHash() + ".torrent";
			//String torrentFile = serverRootFolder + "/" + torrent.getHexInfoHash() + ".torrent";
		    FileOutputStream fos = new FileOutputStream( torrentFile );
		    torrent.save( fos );		    
			fos.close();
			
			logger.debug("saved to " + torrentFile );
	
			return torrent;
		} else {
			return null;
		}
	}
	

	public void setTrackerUrl( String trackerAnnounceUrl ) {
		try {
			URI uri = new URI( trackerAnnounceUrl );
			this.trackerAnnounceUrl = uri;
			this.canCreateTorrent = true;
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}	
	
	public void shareFile( String torrentFile ) throws Exception {
		logger.debug("starting to share torrent " + torrentFile );
		this.torrentFile = torrentFile;
		File tf = new File(torrentFile);
		Torrent tr = Torrent.load( tf, true ); 
		String parentFolder = tr.getCreatedBy();

		File targetContentFolder = new File( storageFolder + "/" + parentFolder );

		SharedTorrent st = new SharedTorrent( tr, targetContentFolder );
		client = new Client( bindAddress, st );
		client.share(); 
	}
	
    private InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = en.nextElement();
            for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements();) {
                InetAddress addr = en2.nextElement();
                if (!addr.isLoopbackAddress()) {
                    if (addr instanceof Inet4Address) {
                        if (preferIPv6) {
                            continue;
                        }
                        return addr;
                    }
                    if (addr instanceof Inet6Address) {
                        if (preferIpv4) {
                            continue;
                        }
                        return addr;
                    }
                }
            }
        }
        return null;
    }    

}
