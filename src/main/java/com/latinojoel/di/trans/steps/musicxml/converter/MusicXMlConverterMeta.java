package com.latinojoel.di.trans.steps.musicxml.converter;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

import com.latinojoel.di.ui.trans.steps.musicxml.MusicXMLConverterDialog;

/**
 * This class is responsible for implementing functionality regarding step meta. All Kettle steps
 * have an extension of this where private fields have been added with public accessors.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.0
 */
@Step(id = "MusicXMLConverterStep", name = "MusicXMLConverterStep.Step.Name",
    description = "MusicXMLConverterStep.Step.Description",
    categoryDescription = "MusicXMLConverterStep.Step.Category",
    image = "com/latinojoel/di/trans/steps/musicxml/MusicXMLConverterStep.png",
    i18nPackageName = "com.latinojoel.di.trans.steps.musicxml",
    casesUrl = "https://github.com/latinojoel", documentationUrl = "https://github.com/latinojoel",
    forumUrl = "https://github.com/latinojoel")
public class MusicXMlConverterMeta extends BaseStepMeta implements StepMetaInterface {

  /** for i18n purposes. **/
  private static final Class<?> PKG = MusicXMlConverterMeta.class;

  private String musicXMLField, convertTypeField, saveFileField;


  public String getMusicXMLField() {
    return musicXMLField;
  }

  public void setMusicXMLField(String musicXMLField) {
    this.musicXMLField = musicXMLField;
  }

  public String getConvertTypeField() {
    return convertTypeField;
  }

  public void setConvertTypeField(String convertTypeField) {
    this.convertTypeField = convertTypeField;
  }

  public String getSaveFileField() {
    return saveFileField;
  }

  public void setSaveFileField(String saveFileField) {
    this.saveFileField = saveFileField;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getXML() {
    final StringBuilder retval = new StringBuilder();
    retval.append("    " + XMLHandler.addTagValue("musicXMLField", musicXMLField));
    retval.append("    " + XMLHandler.addTagValue("convertTypeField", convertTypeField));
    retval.append("    " + XMLHandler.addTagValue("saveFileField", saveFileField));

    return retval.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @throws KettleException
   */
  @Override
  public void readRep(Repository rep, ObjectId idStep, List<DatabaseMeta> databases,
      Map<String, Counter> counters)
      throws KettleException {
    try {
      musicXMLField = rep.getStepAttributeString(idStep, "musicXMLField");
      convertTypeField = rep.getStepAttributeString(idStep, "convertTypeField");
      saveFileField = rep.getStepAttributeString(idStep, "saveFileField");
    } catch (Exception e) {
      throw new KettleException(BaseMessages.getString(PKG,
          "MusicXMLConverter.Exception.UnexpectedErrorInReadingStepInfo"), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @throws KettleException
   */
  @Override
  public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep)
      throws KettleException {
    try {
      rep.saveStepAttribute(idTransformation, idStep, "musicXMLField", musicXMLField);
      rep.saveStepAttribute(idTransformation, idStep, "convertTypeField", convertTypeField);
      rep.saveStepAttribute(idTransformation, idStep, "saveFileField", saveFileField);
    } catch (Exception e) {
      throw new KettleException(BaseMessages.getString(PKG,
          "MusicXMLConverter.Exception.UnableToSaveStepInfoToRepository") + idStep, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info,
      StepMeta nextStep,
      VariableSpace space) {
    return;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    return super.clone();
  }

  /**
   * {@inheritDoc}
   * 
   * @throws KettleXMLException
   */
  @Override
  public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters)
      throws KettleXMLException {
    readData(stepnode);
  }

  /**
   * Reads data from XML transformation file.
   * 
   * @param stepnode the step XML node.
   * @throws KettleXMLException the kettle XML exception.
   */
  public void readData(Node stepnode) throws KettleXMLException {
    try {
      musicXMLField = XMLHandler.getTagValue(stepnode, "musicXMLField");
      convertTypeField = XMLHandler.getTagValue(stepnode, "convertTypeField");
      saveFileField = XMLHandler.getTagValue(stepnode, "saveFileField");
    } catch (Exception e) {
      throw new KettleXMLException(BaseMessages.getString(PKG,
          "MusicXMLConverter.Exception.UnexpectedErrorInReadingStepInfo"), e);
    }
  }

  /**
   * Sets the default values.
   */
  public void setDefault() {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void check(List<CheckResultInterface> remarks, TransMeta transmeta, StepMeta stepMeta,
      RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {}

  /**
   * Get the Step dialog, needs for configure the step.
   * 
   * @param shell the shell.
   * @param meta the associated base step metadata.
   * @param transMeta the associated transformation metadata.
   * @param name the step name
   * @return The appropriate StepDialogInterface class.
   */
  public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta,
      String name) {
    return new MusicXMLConverterDialog(shell, (BaseStepMeta) meta, transMeta, name);
  }

  /**
   * Get the executing step, needed by Trans to launch a step.
   * 
   * @param stepMeta The step info.
   * @param stepDataInterface the step data interface linked to this step. Here the step can store
   *        temporary data, database connections, etc.
   * @param cnr The copy nr to get.
   * @param transMeta The transformation info.
   * @param disp The launching transformation.
   * @return The appropriate StepInterface class.
   */
  public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr,
      TransMeta transMeta,
      Trans disp) {
    return new MusicXMLConverter(stepMeta, stepDataInterface, cnr, transMeta, disp);
  }

  /**
   * Get a new instance of the appropriate data class. This data class implements the
   * StepDataInterface. It basically contains the persisting data that needs to live on, even if a
   * worker thread is terminated.
   * 
   * @return The appropriate StepDataInterface class.
   */
  public StepDataInterface getStepData() {
    return new MusicXMLConverterData();
  }

}
