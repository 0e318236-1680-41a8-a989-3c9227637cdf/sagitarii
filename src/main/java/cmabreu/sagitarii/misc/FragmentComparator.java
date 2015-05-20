package cmabreu.sagitarii.misc;

import java.util.Comparator;

import cmabreu.sagitarii.persistence.entity.Fragment;

public class FragmentComparator implements Comparator<Fragment> {

	@Override
	public int compare(Fragment o1, Fragment o2) {
		if ( o1.getIndexOrder() == o2.getIndexOrder() ) { return 0; }
		if ( o1.getIndexOrder() < o2.getIndexOrder() ) { return -1; }
		if ( o1.getIndexOrder() > o2.getIndexOrder() ) { return 1; }
		return 0;
	}
}
