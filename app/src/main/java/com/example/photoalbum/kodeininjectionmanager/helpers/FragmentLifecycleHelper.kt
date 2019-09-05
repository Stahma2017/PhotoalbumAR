package com.example.photoalbum.kodeininjectionmanager.helpers

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.photoalbum.kodeininjectionmanager.IHasRetainedKodein
import com.example.photoalbum.kodeininjectionmanager.KodeinsStore

internal class FragmentLifecycleHelper(private val kodeinsStore: KodeinsStore) :
    FragmentManager.FragmentLifecycleCallbacks() {
    private val fragmentsSavedInstances: MutableSet<String> = HashSet()

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)
        (f as? IHasRetainedKodein)?.let { fragmentsSavedInstances.remove(it.getKodeinKey()) }
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)
        (f as? IHasRetainedKodein)?.let { fragmentsSavedInstances.remove(it.getKodeinKey()) }
    }

    override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
        super.onFragmentSaveInstanceState(fm, f, outState)
        (f as? IHasRetainedKodein)?.let { fragmentsSavedInstances.add(it.getKodeinKey()) }
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        if (f !is IHasRetainedKodein) return

        if (f.requireActivity().isFinishing) {
            Log.d("FragmentLifecycleHelper", "Kodein for key=${f.getKodeinKey()} removing.")
            kodeinsStore.remove(f.getKodeinKey())
            return
        }

        if (isPrincipalFinishing(f, fragmentsSavedInstances.contains(f.getKodeinKey()))) {
            Log.d("FragmentLifecycleHelper", "Kodein for key=${f.getKodeinKey()} removing.")
            kodeinsStore.remove(f.getKodeinKey())
        }

        fragmentsSavedInstances.remove(f.getKodeinKey())
    }

    private fun isPrincipalFinishing(fragment: Fragment, wasInstanceStateSaved: Boolean) =
        fragment.activity!!.isFinishing || (!wasInstanceStateSaved // See http://stackoverflow.com/questions/34649126/fragment-back-stack-and-isremoving
                && (fragment.isRemoving || isAnyParentOfFragmentRemoving(fragment)))

    private fun isAnyParentOfFragmentRemoving(fragment: Fragment): Boolean {
        var isAnyParentRemoving = false

        var parent = fragment.parentFragment
        while (!isAnyParentRemoving && parent != null) {
            isAnyParentRemoving = parent.isRemoving
            parent = parent.parentFragment
        }
        return isAnyParentRemoving
    }
}