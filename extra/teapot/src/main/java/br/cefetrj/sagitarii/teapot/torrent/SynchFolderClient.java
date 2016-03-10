package br.cefetrj.sagitarii.teapot.torrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.teapot.ZipUtil;

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
	private List<Client> clients;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public boolean isSharing( String torrentHash ) {
		cleanCompletedMonitors();
		for ( Client client : getClients() ) {
			if ( client.getTorrent().getHexInfoHash().toLowerCase().equals( torrentHash.toLowerCase() ) ) {
				return true;
			}
		} 
		return false;
	}
	
	public List<Client> getClients() {
		return new ArrayList<Client>( clients ); 
	}
	
	public synchronized void show() {
		for ( Client client : getClients() ) {
			//ClientState state = client.getState();
			//float completion = client.getTorrent().getCompletion();
			try {
				boolean canClose = ( client.getPeers().size() > 0 );
				for ( SharingPeer sp : client.getPeers() ) {
					//logger.debug("    > " + sp.getIp() + " " + sp.isConnected() + " " + sp.isDownloading() );
					if ( sp.isConnected() || sp.isDownloading() ) {
						canClose = false;
					}
				}
				if ( client.getPeers().size() == 1 ) canClose = false;
				logger.debug(" > " + client.getTorrent().getCreatedBy() + ": " + client.getPeers().size() + " peers. (" + canClose + ")");
			} catch ( Exception e ) {
				logger.error( e.getMessage() );
			}
		}
	}
	
	public synchronized void cleanCompletedMonitors() {
		for ( Client client : getClients() ) {
			if ( client.getState() == ClientState.DONE ) {
				logger.debug("Stop finished sharing: " + client.getTorrent().getName() );
				client.stop();
				clients.remove( client );
				client = null;
				break;
			}
			
		} 
	}
	
	public void stopAll() {
		for ( Client client : getClients() ) {
			client.stop();
		}
		clients.clear();
	}
	
	private void config( String rootFolder, InetAddress bindAddress ) throws Exception {
		this.serverRootFolder = rootFolder;
		this.bindAddress = bindAddress;
		
		this.storageFolder = serverRootFolder;
		this.clients = new ArrayList<Client>();

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
	
	private void startMonitor( Client seeder ) {
		clients.add( seeder );
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
			
			String torrentFile = serverRootFolder + "/" + torrent.getHexInfoHash() + ".torrent";
		    FileOutputStream fos = new FileOutputStream( torrentFile );
		    torrent.save( fos );		    
			fos.close();
			
			logger.debug("saved to " + torrentFile );
	
			shareFile( torrentFile );
			return torrent;
		} else {
			return null;
		}
	}
	
	public void saveTorrentDownloadAndShare( TorrentFile torrentFile ) throws Exception  {
		String fileName = torrentFile.getHash();
		fileName = serverRootFolder + "/" + fileName + ".torrent";
		
		File file = new File(fileName);
		if ( file.exists() ) {
			//log(" > Already here.");
			return;
		}
		
		FileUtils.writeByteArrayToFile( file , ZipUtil.toByteArray( torrentFile.getFileContent() ) );
		shareFile( fileName );
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
	
	public String createTorrentFromFile( String fileName, String author ) throws Exception {
		if ( !canCreateTorrent ) throw new Exception("Cannot create torrent without Announce URL");

		String fullFileName = storageFolder + "/" + fileName;
		String torrentFile = serverRootFolder + "/" + fileName + ".torrent";
		
		Torrent torrent = Torrent.create( new File( fullFileName ), trackerAnnounceUrl, author );
		
	    FileOutputStream fos = new FileOutputStream( torrentFile );
	    torrent.save( fos );		    
		fos.close();
		return torrentFile;
	}
	
	

	public void startShare() throws Exception {
	    FilenameFilter filter = new FilenameFilter() {
	    	  @Override
	    	  public boolean accept(File dir, String name) {
	    		  boolean result = name.endsWith(".torrent");
	    		  return result;
	    	  }
	    };

	    for (File f : new File( serverRootFolder ).listFiles(filter) ) {
	    	//log("Start sharing " + f.getName() );
	    	shareFile( serverRootFolder + "/" + f.getName() );
	    }

	}
	
	public Client shareFile( String torrentFile) throws Exception {
		logger.debug("sharing " + torrentFile );
		
		File tf = new File(torrentFile);
		Torrent tr = Torrent.load( tf );
		String parentFolder = tr.getCreatedBy();

		File targetContentFolder = new File( storageFolder + "/" + parentFolder );

		targetContentFolder.mkdirs();
		SharedTorrent st = SharedTorrent.fromFile( tf, targetContentFolder );
		Client seeder = new Client( bindAddress, st);
	    
		seeder.share(60); 
	    
		startMonitor( seeder );
	    return seeder;
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
