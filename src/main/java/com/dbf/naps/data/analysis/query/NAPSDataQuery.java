package com.dbf.naps.data.analysis.query;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.dbf.naps.data.analysis.AggregateFunction;
import com.dbf.naps.data.analysis.DataAnalysisOptions;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.exporter.NAPSDataExtractor;
import com.dbf.naps.data.records.DataRecordGroup;
import com.dbf.naps.data.utilities.Utils;

public abstract class NAPSDataQuery<O extends DataAnalysisOptions> extends NAPSDataExtractor<O> {

	public NAPSDataQuery(String[] args) {
		super(args);
	}
	
	@Override
	protected List<DataRecordGroup> getDataGroups() {
		try(SqlSession session = getSqlSessionFactory().openSession(true)) {
			return session.getMapper(DataMapper.class).getExportDataGroups(
					getOptions().getYearStart(), getOptions().getYearEnd(), getOptions().getPollutants(),  getOptions().getSites(),			//Per-file filters
					getOptions().isFilePerYear(), getOptions().isFilePerPollutant(), getOptions().isFilePerSite(),							//Grouping
					getOptions().getMethods(), getOptions().getReportTypes(),                                                               //Method filters
					getOptions().getMonths(), getOptions().getDaysOfMonth(), getOptions().getDaysOfWeek(),									//Basic filters
					getOptions().getSiteName(), getOptions().getCityName(), getOptions().getProvTerr().stream().map(p->p.name()).toList(),  //Basic filters
					getOptions().getSiteType().stream().map(s->s.name()).toList(),															//Advanced site filters
					getOptions().getUrbanization().stream().map(u->u.name()).toList(),														//Advanced site filters
					getOptions().getValueUpperBound(), getOptions().getValueLowerBound(), 													//Advanced data filters
					getDataset(),
					getOptions().isAQHI());
		}
	}
	
	@Override
	protected void appendFilename(StringBuilder fileName, Integer year, String pollutant, Integer site) {
		super.appendFilename(fileName, year, pollutant, site);
		
		if(getOptions().getFileName() == null) {
			//Not using a custom filename
			boolean hasFunction = getOptions().getAggregateFunction() != null && !getOptions().getAggregateFunction().equals(AggregateFunction.NONE);
			if(hasFunction) {
				fileName.append("_");
				//Convert the function name to lower case, except for the first character: AVG -> Avg, COUNT -> Count
				String funcName = getOptions().getAggregateFunction().name().substring(0, 1) + getOptions().getAggregateFunction().name().substring(1).toLowerCase();
				fileName.append(funcName);
			}
			if(getOptions().getFields() != null && !getOptions().getFields().isEmpty()) {
				if(!hasFunction) {
					fileName.append("_");
				} else {
					fileName.append(" ");
				}
				fileName.append("By ");
				Utils.prettyPrintStringList(getOptions().getFields().stream().map(f->f.getPrettyName()).toList(), fileName, false);
			}
		}
	}
}
