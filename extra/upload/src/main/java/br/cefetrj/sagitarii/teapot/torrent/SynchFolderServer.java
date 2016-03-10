package br.cefetrj.sagitarii.teapot.torrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import br.cefetrj.sagitarii.teapot.ZipUtil;

import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.TrackedPeer;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;


// http://www.tools4noobs.com/online_tools/torrent_decode/
// https://github.com/fujohnwang/fujohnwang.github.com/blob/master/posts/2012-04-09-playing-bittorrent-with-ttorrent.md
// https://github.com/mpetazzoni/ttorrent

public class SynchFolderServer {
	private String serverRootFolder;
	private String storageFolder;
	private Tracker tracker;

	private void config( String rootFolder, InetAddress bindAddress ) throws Exception {
		this.serverRootFolder = rootFolder;
		this.storageFolder = serverRootFolder + "/storage" ;
		try {
			new File( storageFolder ).mkdirs();
		} catch ( Exception e ) { 
			//
		}
		tracker = new Tracker( bindAddress );
	}
	
	public SynchFolderServer( String rootFolder, InetAddress bindAddress ) throws Exception {
		config( rootFolder, bindAddress );
	}
	
	public SynchFolderServer( String rootFolder ) throws Exception {
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

	public String createTorrentFromFolder( String folderPath, String torrentFileName, String author ) throws Exception {
		String sourceFolder = storageFolder + "/" + folderPath;
		Torrent torrent = Torrent.create(
				new File(sourceFolder), 
				getFiles( sourceFolder ), 
				tracker.getAnnounceUrl().toURI(), author);
		String torrentFile = serverRootFolder + "/" + torrentFileName;
	    FileOutputStream fos = new FileOutputStream( torrentFile );
	    torrent.save( fos );		    
		fos.close();
		return torrentFile;
	}
	
	public void showTrackedTorrents() {
		Collection<TrackedTorrent> trackedTorrents = tracker.getTrackedTorrents();
		for ( TrackedTorrent tr : trackedTorrents ) {
			System.out.println( "> Name : " + tr.getName() );
			for ( Entry<String,TrackedPeer> peer : tr.getPeers().entrySet() ) {
				System.out.println( " > Peer:  " + peer.getValue().getIp() + " " + peer.getValue().isCompleted() );
			}
		}
	}
	
	public String getAnnounceUrl() {
		return tracker.getAnnounceUrl().toString();
	}

	public void stopTracker() {
		tracker.stop();
		System.out.println("> Tracker stopped.");
	}

	public void saveTorrentAndAddTorrentFileToTracker(TorrentFile torrentFile) throws Exception {
		System.out.println("> New Torrent file incomming : " + torrentFile.getFileName() );
		String fileName = torrentFile.getHash();
		fileName = serverRootFolder + "/" + fileName + ".torrent";

		File file = new File(fileName);
		if ( file.exists() ) {
			System.out.println(" > Already here.");
			return;
		}
		
		FileUtils.writeByteArrayToFile( file, ZipUtil.toByteArray( torrentFile.getFileContent() ) );
		tracker.announce( TrackedTorrent.load( file ) );
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
	
	public void startTracker() throws Exception {

	    FilenameFilter filter = new FilenameFilter() {
	    	  @Override
	    	  public boolean accept(File dir, String name) {
	    		  boolean result = name.endsWith(".torrent");
	    		  return result;
	    	  }
	    };
	    
	    for (File f : new File( serverRootFolder ).listFiles(filter) ) {
	    	System.out.println("Announce " + f.getName() );
	    	tracker.announce( TrackedTorrent.load(f) );
	    }
	    
		tracker.start();
		
		System.out.println("> Tracker running.");
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
