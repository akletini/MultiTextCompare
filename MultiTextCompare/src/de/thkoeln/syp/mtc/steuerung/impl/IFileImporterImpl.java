package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import de.thkoeln.syp.mtc.datenhaltung.impl.IConfigImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

public class IFileImporterImpl implements IFileImporter {

	private IConfigImpl iConfigImpl;
	private List<File> textdateien;

	@Override
	public IConfigImpl getConfig() {
		return iConfigImpl;
	}

	public boolean setConfig(IConfigImpl iConfigImpl) {
		if (iConfigImpl != null) {
			this.iConfigImpl = iConfigImpl;
			return true;
		}
		return false;
	}

	@Override
	public List<File> getTextdateien() {
		return textdateien;
	}

	@Override
	public boolean importConfigdatei() {
		File configFile = new File("config\\config.properties");
		try {

			FileReader reader = new FileReader(configFile);

			Properties props = new Properties();

			props.load(reader);

			String savedRootDir = props.getProperty("savedRootDir");
			boolean beachteLeerzeichen = Boolean.parseBoolean(props
					.getProperty("beachteLeerzeichen"));
			boolean beachteGrossschreibung = Boolean.parseBoolean(props
					.getProperty("beachteGrossschreibung"));
			boolean beachteSatzzeichen = Boolean.parseBoolean(props
					.getProperty("beachteSatzzeichen"));

			reader.close();

			iConfigImpl.setRootDir(savedRootDir);
			iConfigImpl.setBeachteLeerzeichen(beachteLeerzeichen);
			iConfigImpl.setBeachteGrossschreibung(beachteGrossschreibung);
			iConfigImpl.setBeachteSatzzeichen(beachteSatzzeichen);

		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
			return false;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean importTextdateien() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean importTextRoot() {
		// TODO Auto-generated method stub
		return false;
	}

}
