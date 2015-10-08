package br.cefetrj.parser.sagitarii;

import br.cefetrj.parser.ActivityList;
import br.cefetrj.parser.sagitarii.SagitariiParser.ActivitySentenceContext;
import br.cefetrj.parser.sagitarii.SagitariiParser.InputSentenceContext;
import br.cefetrj.parser.sagitarii.SagitariiParser.ParallelActivitySentenceContext;

public class SagitariiParserListener extends SagitariiBaseListener {
	private ActivityList list = new ActivityList();

	public void showList() {
		list.showList();
	}
	
	@Override 
	public void enterActivitySentence( ActivitySentenceContext ctx ) { 
		System.out.println( "START NEW ACTIVITY: " + ctx.getText() );
		
		System.out.println(" >> " + ctx.executor().getText() );
		for ( InputSentenceContext isc : ctx.inputSentence() ) {
			if( isc.activitySentence() != null ) {
				System.out.println(" >> AS " + isc.activitySentence().getText() );
			}
			if( isc.input_relation() != null ) {
				System.out.println(" >> IR " + isc.input_relation().getText() );
			}
		}
		System.out.println(" >> " + ctx.output_relation().getText() );
		System.out.println(" >> " + ctx.activity_name().getText() );
		
	}
	
	@Override 
	public void enterParallelActivitySentence( ParallelActivitySentenceContext ctx ) { 
		System.out.println( "START NEW PARALLEL ACTIVITY: " + ctx.getText() + " " + ctx.getChildCount() );
		System.out.println(" >> " + ctx.activitySentence().executor().getText() );
		for ( InputSentenceContext isc : ctx.activitySentence().inputSentence() ) {
			if( isc.activitySentence() != null ) {
				System.out.println(" >> AS " + isc.activitySentence().getText() );
			}
			if( isc.input_relation() != null ) {
				System.out.println(" >> IR " + isc.input_relation().getText() );
			}
		}
		System.out.println(" >> " + ctx.activitySentence().output_relation().getText() );
		System.out.println(" >> " + ctx.activitySentence().activity_name().getText() );
	}
	

}
