package uk.ac.standrews.cs.filesystem.sosfilesystem;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.exceptions.PersistenceException;
import uk.ac.standrews.cs.filesystem.FileSystemConstants;
import uk.ac.standrews.cs.filesystem.interfaces.IFile;
import uk.ac.standrews.cs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.persistence.interfaces.IData;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.util.Attributes;
import uk.ac.standrews.cs.util.Error;
import uk.ac.standrews.cs.webdav.impl.InputStreamData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFile extends SOSFileSystemObject implements IFile {

    private Version version;

    boolean isCompoundData;
    private Atom atom;
    private Collection<Content> atoms;
    private IAttributedStatefulObject previous;

    public SOSFile(Client sos, IData data) throws PersistenceException {
        super(sos, data);
        this.isCompoundData = false;

        try {
            atom = sos.addAtom(new AtomBuilder().setInputStream(data.getInputStream()));
        } catch (StorageException | IOException | ManifestPersistException e) {
            throw new PersistenceException("SOS atom could not be created");
        }
    }

    public SOSFile(Client sos)  {
        super(sos);
        this.isCompoundData = true;
        this.atoms = new ArrayList<>();
    }

    public SOSFile(Client sos, IGUID guid) {
        super(sos);

        try {
            atom = (Atom) sos.getManifest(guid);
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }
    }

    public SOSFile(Client sos, IData data, IAttributedStatefulObject previous) throws PersistenceException {
        this(sos, data);
        this.previous = previous;
    }

    @Override
    public IAttributes getAttributes() {

        // TODO - iterate over metadata and build attributes
        IAttributes dummyAttributes = new Attributes(FileSystemConstants.ISFILE + Attributes.EQUALS + "true" + Attributes.SEPARATOR +
                FileSystemConstants.CONTENT + Attributes.EQUALS + "text" + Attributes.SEPARATOR );

        return dummyAttributes;
    }

    @Override
    public void setAttributes(IAttributes attributes) {
        // This will allow to explitly set new attributes, resulting in a new version of the data
        // or we could have the data pointing an version, so the file does not need to change as the metadata does
        Error.hardError("unimplemented method");
    }

    @Override
    public long getCreationTime() throws AccessFailureException {
        return 0;
    }

    @Override
    public long getModificationTime() throws AccessFailureException {
        return 0;
    }

    @Override
    public void update(IData data) {
        // TODO - this should result in a new version pointing to a previous one

        throw new NotImplementedException();
    }

    @Override
    public void append(IData data) {
        if (! isCompoundData)
            return;

        try {
            Atom atom = sos.addAtom(new AtomBuilder().setInputStream(data.getInputStream()));
            IGUID guid = atom.getContentGUID();
            Content content = new Content(guid);
            atoms.add(content);

        } catch (ManifestPersistException | IOException | StorageException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void persist() throws PersistenceException {
        try {
            // FIXME - this is a bad way of dealing with previous references.
            Collection<IGUID> prevs = new ArrayList<>();
            if (previous != null) {
                prevs.add(previous.getGUID());
            }

            if (! isCompoundData) {
                IGUID content = atom.getContentGUID();
                version = sos.addVersion(new VersionBuilder(content)
                        .setPrevious(prevs)); // TODO - add metadata

                guid = GUIDFactory.recreateGUID(version.getVersionGUID().toString());
            } else {
                Compound compound = sos.addCompound(CompoundType.DATA, atoms);
                version = sos.addVersion(new VersionBuilder(compound.getContentGUID())
                        .setPrevious(prevs)); // TODO - add metadata

                guid = GUIDFactory.recreateGUID(version.getVersionGUID().toString());
            }

        } catch (ManifestNotMadeException | ManifestPersistException | GUIDGenerationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IData reify() {
        // TODO - look for guid and return idata
        // this will differ based on whether it is a single atom or a compound of atoms
        // sos.getData(guid);

        // NOTE: idea - have a isChunked() method. If that method returns true, then reify returns data until null (no more chunks)
        IData data = new InputStreamData(sos.getAtomContent(atom), 4092); // FIXME - size of data expected should not be hardcoded

        return data;
    }
}
