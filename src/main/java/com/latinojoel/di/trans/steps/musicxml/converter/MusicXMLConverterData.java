package com.latinojoel.di.trans.steps.musicxml.converter;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * This class contains the methods to set and retrieve the status of the step data.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.0
 * 
 */
public class MusicXMLConverterData extends BaseStepData implements StepDataInterface {
  RowMetaInterface outputRowMeta = null;
  RowMetaInterface insertRowMeta = null;
  int fieldnr = 0;
  int nrPrevFields = 0;

  int indexOfMusicXMLField = -1;
  int indexOfSaveFileField = -1;
  String tmpFolderPath;
  String convertType;
}
