/*
 * Copyright 2018 Coffee and Cream Studios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.coffeegerm.glucoseguide.ui.statistics.children

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.coffeegerm.glucoseguide.GlucoseGuide
import io.github.coffeegerm.glucoseguide.R
import io.github.coffeegerm.glucoseguide.data.viewModel.StatisticsViewModel
import io.github.coffeegerm.glucoseguide.data.viewModel.StatisticsViewModelFactory
import io.github.coffeegerm.glucoseguide.utils.DateAssistant
import kotlinx.android.synthetic.main.fragment_three_months_statistics.*
import javax.inject.Inject

class ThreeMonthsStatisticsFragment : Fragment() {
  
  @Inject
  lateinit var dateAssistant: DateAssistant
  @Inject
  lateinit var statisticsViewModelFactory: StatisticsViewModelFactory
  private lateinit var statisticsViewModel: StatisticsViewModel
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    GlucoseGuide.syringe.inject(this)
    statisticsViewModel = ViewModelProviders.of(this, statisticsViewModelFactory).get(StatisticsViewModel::class.java)
  }
  
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_three_months_statistics, container, false)
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val threeMonthsAgo = dateAssistant.getThreeMonthsAgoDate()
    if (statisticsViewModel.getNumberOfEntries(threeMonthsAgo) != 0) {
      three_months_average_value.text = statisticsViewModel.getAverageBloodGlucose(threeMonthsAgo)
      three_months_highest_value.text = statisticsViewModel.getHighestBloodGlucose(threeMonthsAgo)
      three_months_lowest_value.text = statisticsViewModel.getLowestBloodGlucose(threeMonthsAgo)
    }
    
    if (statisticsViewModel.getNumberOfEntries(threeMonthsAgo) > 300) {
      a_one_c.text = statisticsViewModel.getA1C()
    }
  }
}
