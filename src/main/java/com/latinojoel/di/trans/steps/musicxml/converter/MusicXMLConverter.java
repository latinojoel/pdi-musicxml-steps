package com.latinojoel.di.trans.steps.musicxml.converter;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jfugue.MusicStringRenderer;
import org.jfugue.MusicXmlParser;
import org.jfugue.Pattern;
import org.jfugue.Player;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;


/**
 * This class is responsible to processing the data rows.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.0
 */
public class MusicXMLConverter extends BaseStep implements StepInterface {

  /** for i18n purposes. **/
  private static final Class<?> PKG = MusicXMLConverter.class;
  private MusicXMlConverterMeta meta;
  private MusicXMLConverterData data;

  public MusicXMLConverter(
      StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
    super(s, stepDataInterface, c, t, dis);
  }

  /**
   * {@inheritDoc}
   * 
   * @throws KettleException
   */
  @Override
  public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
    this.meta = (MusicXMlConverterMeta) smi;
    this.data = (MusicXMLConverterData) sdi;

    final Object[] r = getRow();
    if (r == null) {
      setOutputDone();
      return false;
    }

    if (first) {
      first = false;
      data.outputRowMeta = super.getInputRowMeta().clone();
      data.nrPrevFields = data.outputRowMeta.size();
      meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

      cachePosition();
    } // end if first

    final Object[] outputRow = RowDataUtil.allocateRowData(data.outputRowMeta.size());
    for (int i = 0; i < data.nrPrevFields; i++) {
      outputRow[i] = r[i];
    }

    try {
      final RowMetaInterface rowMeta = getInputRowMeta().clone();
      final String musicXMLField = rowMeta.getString(r, data.indexOfMusicXMLField);

      // Process MusicXML file path
      String musicFilePath = musicXMLField;
      if (musicXMLField.lastIndexOf(".mxl") == musicXMLField.length() - 4) {
        data.tmpFolderPath =
            System.getProperty("java.io.tmpdir") + File.separator + "MusicXMLConverterPDIStep"
                + Calendar.getInstance().getTimeInMillis();
        unZipIt(musicXMLField, data.tmpFolderPath);
        final FileFilter filter = new FileFilter() {
          public boolean accept(File pathname) {
            return pathname.getAbsolutePath().lastIndexOf(".xml") == pathname.getAbsolutePath()
                .length() - 4;
          }
        };
        final File[] files = new File(data.tmpFolderPath).listFiles(filter);
        if (files.length > 0) {
          musicFilePath = files[0].getAbsolutePath();
        } else {
          logError("MusicXML file not found.");
        }
      }
      if ("Just play".equalsIgnoreCase(data.convertType)) {
        final Pattern song = read(new File(musicFilePath));
        final Player player = new Player();
        player.play(song);
      } else if ("Convert MusicXML to MIDI".equalsIgnoreCase(data.convertType)) {
        final Pattern song = read(new File(musicFilePath));
        final Player player = new Player();
        player.saveMidi(song, new File(rowMeta.getString(r, data.indexOfSaveFileField)));
      } else if ("Convert MusicXML to MusicString".equalsIgnoreCase(data.convertType)) {
        final Pattern song = read(new File(musicFilePath));
        final PrintWriter writer =
            new PrintWriter(new File(rowMeta.getString(r, data.indexOfSaveFileField)), "UTF-8");
        writer.print(song.getMusicString());
        writer.close();
      }
      if (data.tmpFolderPath != null) {
        rmdir(new File(data.tmpFolderPath));
      }

    } catch (Exception e) {
      logError(e.getMessage(), e);
      if (data.tmpFolderPath != null) {
        rmdir(new File(data.tmpFolderPath));
      }
    }
    putRow(data.outputRowMeta, outputRow);

    if (checkFeedback(getLinesRead())) {
      if (log.isBasic()) {
        logBasic("Linenr " + getLinesRead()); // Some basic logging
      }
    }
    return true;
  }

  /**
   * Extract ZIP file..
   * 
   * @param zipFile ZIP file path.
   * @param outputFolder Target folder to extract.
   * @throws IOException
   */
  public void unZipIt(String zipFile, String outputFolder) throws IOException {
    final File destDir = new File(outputFolder);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }
    final ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
    ZipEntry entry = zipIn.getNextEntry();
    // iterates over entries in the zip file
    while (entry != null) {
      final String filePath = outputFolder + File.separator + entry.getName();

      if (!entry.isDirectory()) {
        // if the entry is a file, extracts it
        extractFile(zipIn, filePath);
      } else {
        // if the entry is a directory, make the directory
        final File dir = new File(filePath);
        dir.mkdir();
      }
      zipIn.closeEntry();
      entry = zipIn.getNextEntry();
    }
    zipIn.close();

    log.logDebug("Done");

  }

  /**
   * Extracts a zip entry (file entry).
   * 
   * @param zipIn
   * @param filePath
   * @throws IOException
   */
  private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
    final File out = new File(filePath);
    out.getParentFile().mkdirs();
    final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out));
    final byte[] bytesIn = new byte[4096];
    int read = 0;
    while ((read = zipIn.read(bytesIn)) != -1) {
      bos.write(bytesIn, 0, read);
    }
    bos.close();
  }

  /**
   * Empty and delete a folder (and subfolders).
   * 
   * @param folder folder to empty
   */
  public void rmdir(final File folder) {
    // check if folder file is a real folder
    if (folder.isDirectory()) {
      final File[] list = folder.listFiles();
      if (list != null) {
        for (int i = 0; i < list.length; i++) {
          final File tmpF = list[i];
          if (tmpF.isDirectory()) {
            rmdir(tmpF);
          }
          tmpF.delete();
        }
      }
      if (!folder.delete()) {
        logBasic("can't delete folder : " + folder);
      }
    }
  }

  /**
   * Parse MusicXML file.
   * 
   * @param inputFile input file.
   * @return the music pattern.
   * @throws Exception
   */
  private static Pattern read(File inputFile) throws Exception {
    final MusicStringRenderer renderer = new MusicStringRenderer();
    final MusicXmlParser parser = new MusicXmlParser();
    parser.addParserListener(renderer);
    parser.parse(inputFile);
    return renderer.getPattern();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
    meta = (MusicXMlConverterMeta) smi;
    data = (MusicXMLConverterData) sdi;

    if (super.init(smi, sdi)) {
      if (meta.getConvertTypeField() != null) {
        data.convertType = meta.getConvertTypeField();
      }
      return true;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
    super.dispose(smi, sdi);
  }

  /**
   * Run is were the action happens.
   */
  public void run() {
    logBasic("Starting to run...");
    try {
      while (processRow(meta, data) && !isStopped()) {
        continue;
      }
    } catch (Exception e) {
      logError("Unexpected error : " + e.toString());
      logError(Const.getStackTracker(e));
      setErrors(1);
      stopAll();
    } finally {
      dispose(meta, data);
      logBasic("Finished, processing " + getLinesRead() + " rows");
      markStop();
    }
  }

  /**
   * Checks the fields positions.
   * 
   * @throws KettleStepException the kettle step exception.
   */
  private void cachePosition() throws KettleStepException {
    if (meta.getMusicXMLField() != null && data.indexOfMusicXMLField < 0) {
      data.indexOfMusicXMLField = getInputRowMeta().indexOfValue(meta.getMusicXMLField());
      if (data.indexOfMusicXMLField < 0) {
        final String message =
            "Unable to find table name field [" + meta.getMusicXMLField() + "] in input row";
        logError(message);
        throw new KettleStepException(message);
      }
    }

    if (meta.getSaveFileField() != null && data.indexOfSaveFileField < 0) {
      data.indexOfSaveFileField = getInputRowMeta().indexOfValue(meta.getSaveFileField());
      if (data.indexOfSaveFileField < 0) {
        final String message =
            "Unable to find table name field [" + meta.getSaveFileField() + "] in input row";
        logError(message);
        throw new KettleStepException(message);
      }
    }
  }
}
