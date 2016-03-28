/*
 * Created on Jul 5, 2005 at 10:14:34 AM.
 */
package uk.ac.standrews.cs.filesystem.absfilesystem.util;


/**
* Creates a Directory in an extant file system.
* 
 * @author al
 */
public class MkDir {
    
//    private static String RECURSIVE = "-r";
//    
//    public static void main( String[] args ) {    
//        boolean recursive = false;  // do we make the directories on the path leading to root.
//        
//        Diagnostic.setLevel(Diagnostic.FULL);
//        
//        if( args.length < 1 || args.length >2 ) {
//            Error.hardError( "Usage - MKDir ["+RECURSIVE+"] <pathname>");
//        }
//        String flag = args[0];
//        if( flag == RECURSIVE ) {
//            recursive = true;
//            if(args.length!=2){
//                Error.hardError( "Usage - MKDir ["+RECURSIVE+"] <pathname>");
//            }
//        }
//        
//        Diagnostic.trace( "Making Store", Diagnostic.FULL );
//        IRootedGUIDStore store = LocalFileBasedStoreFactory.makeStore();
//        IFileSystem fs = null;
//		try {
//			fs = new StoreBasedFileSystem(store);
//		} catch (StoreIntegrityException e) {
//			Error.hardExceptionError("Corrupt store", e);
//		}
// 
//        Diagnostic.trace( "Getting root directory", Diagnostic.FULL );
//        IDirectory root = null;
//        try {
//            root = fs.getRootDirectory();
//        } catch (Exception e) {
//            Error.hardExceptionError( "Cannot get root directory", e);
//        }
//        Assert.assertion( root != null, "Root Directory is null" );
//        
//        StringTokenizer st = new StringTokenizer( args[recursive?1:0], "/" );
//        while( st.hasMoreTokens() ) {
//            String nextDirName = st.nextToken();
//            Diagnostic.trace( "Lookning up: " + nextDirName,Diagnostic.FULL );
//            IAttributedStatefulObject apo = root.get( nextDirName );
//            if( apo == null ) {
//                Diagnostic.trace( "APO: " + nextDirName + " is null",Diagnostic.FULL );
//                if( recursive || ( ! st.hasMoreTokens() ) ) { // either recursively creating or at end of path
//                    Diagnostic.trace( "Creating collection: " + nextDirName,Diagnostic.FULL );
//                    try {
//                        fs.createNewDirectory( root, nextDirName, null);
//                    } catch (Exception e) {
//                        Error.hardExceptionError( "Creating collection", e );
//                    }
//                    Diagnostic.trace( "Created collection: " + nextDirName + " looking it up",Diagnostic.FULL );
//                    apo = root.get( nextDirName );
//                } else {
//                        Error.hardError( "Encountered Directory that doesn't exist when in non-recursive mode");
//                }
//            }
//            if( apo instanceof IDirectory ) {
//                Diagnostic.trace( "APO is Directory: " + nextDirName,Diagnostic.FULL );
//                root = (IDirectory) apo;
//            } else if( apo instanceof IFile ){
//                Error.hardError( "Encountered a file when attempting to create directory");
//            } else {
//                Error.hardError( "Encountered unknown IAttributedStatefulObject when attempting to create directory" + apo.getClass().getName() );
//            }
//        }
//    }
}
