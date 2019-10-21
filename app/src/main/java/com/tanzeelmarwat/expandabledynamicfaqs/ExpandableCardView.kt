package com.tanzeelmarwat.expandabledynamicfaqs


import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.animation.PathInterpolatorCompat
import kotlinx.android.synthetic.main.expandable_cardview.view.*

/**
 *
 * Copyright (c) 2018 Alessandro Sperotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Created by alessandros on 23/02/2018.
 * Modified by Adrian Devezin on 24/12/2018.
 * @author Alessandro Sperotti
 */

class ExpandableCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private var title: String? = null

    private var innerView: View? = null
    private var layoutView: View? = null

    private var typedArray: TypedArray? = null
    private var innerViewRes: Int = 0
    private var layoutViewRes: Int = 0
    private var iconDrawable: Drawable? = null

    var animDuration = DEFAULT_ANIM_DURATION.toLong()

    var isExpanded = false
        private set
    private var isExpanding = false
    private var isCollapsing = false
    private var expandOnClick = false
    private var startExpanded = false

    private var previousHeight = 0

    private var listener: OnExpandedListener? = null
    private var clickListener: OnClickListener? = null

    private var easeInOutQuart = PathInterpolatorCompat.create(0.77f, 0f, 0.175f, 1f)

    private var defaultClickListener = OnClickListener {
        clickListener?.let { clickListener ->
            clickListener.onClicked(isExpanded)
        }

        if (isExpanded)
            collapse()
        else
            expand()
    }

    private val isMoving: Boolean
        get() = isExpanding || isCollapsing


    init {
        attrs?.let {
            initAttributes(context, attrs)
        }
        initView(context)
    }

    private fun initView(context: Context) {
        //Inflating View
        LayoutInflater.from(context).inflate(layoutViewRes, this)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet) {
        //Ottengo attributi
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableCardView)
        this@ExpandableCardView.typedArray = typedArray
        title = typedArray.getString(R.styleable.ExpandableCardView_title)
        iconDrawable = typedArray.getDrawable(R.styleable.ExpandableCardView_icon)
        innerViewRes = typedArray.getResourceId(R.styleable.ExpandableCardView_inner_view, View.NO_ID)
        layoutViewRes = typedArray.getResourceId(R.styleable.ExpandableCardView_layout_view, View.NO_ID)
        expandOnClick = typedArray.getBoolean(R.styleable.ExpandableCardView_expandOnClick, false)
        animDuration = typedArray.getInteger(R.styleable.ExpandableCardView_animationDuration, DEFAULT_ANIM_DURATION).toLong()
        startExpanded = typedArray.getBoolean(R.styleable.ExpandableCardView_startExpanded, false)
        typedArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        //Setting attributes
        if (! TextUtils.isEmpty(title)) card_title.text = title

        iconDrawable?.let { drawable ->
            card_header.visibility = View.VISIBLE
            card_icon.background = drawable
        }

        setInnerView(innerViewRes)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            elevation = convertDpToPixels(context, 4f)

        if (startExpanded) {
            animDuration = 0
            expand()
        }

        if (expandOnClick) {
            card_layout.setOnClickListener(defaultClickListener)
            card_arrow.setOnClickListener(defaultClickListener)
        }

    }

    fun expand() {
        val initialHeight = card_layout.height
        if (! isMoving) {
            previousHeight = initialHeight
        }

        card_layout.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight = card_layout.measuredHeight

        if (targetHeight - initialHeight != 0) {
            animateViews(initialHeight,
                    targetHeight - initialHeight,
                    EXPANDING)
        }
    }

    fun expandToNewHeight() {
        val initialHeight = card_layout.height

        card_layout.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight = card_layout.measuredHeight

        if (targetHeight - initialHeight != 0) {
            animateViews(initialHeight,
                    targetHeight - initialHeight,
                    EXPANDING)
        }
    }

    fun collapse() {
        val initialHeight = card_layout.measuredHeight
        if (initialHeight - previousHeight != 0) {
            animateViews(initialHeight,
                    initialHeight - previousHeight,
                    COLLAPSING)
        }
    }

    private fun animateViews(initialHeight: Int, distance: Int, animationType: Int) {

        val expandAnimation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    //Setting isExpanding/isCollapsing to false
                    isExpanding = false
                    isCollapsing = false

                    listener?.let { listener ->
                        if (animationType == EXPANDING) {
                            listener.onExpandChanged(card_layout, true)
                        } else {
                            listener.onExpandChanged(card_layout, false)
                        }
                    }
                }

                card_layout.layoutParams.height = if (animationType == EXPANDING)
                    (initialHeight + distance * interpolatedTime).toInt()
                else
                    (initialHeight - distance * interpolatedTime).toInt()
                card_container.requestLayout()

                card_container.layoutParams.height = if (animationType == EXPANDING)
                    (initialHeight + distance * interpolatedTime).toInt()
                else
                    (initialHeight - distance * interpolatedTime).toInt()

            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        val arrowAnimation = if (animationType == EXPANDING)
            RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)
        else
            RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)

        arrowAnimation.fillAfter = true


        arrowAnimation.duration = animDuration
        expandAnimation.duration = animDuration

        expandAnimation.interpolator = easeInOutQuart

        isExpanding = animationType == EXPANDING
        isCollapsing = animationType == COLLAPSING

        startAnimation(expandAnimation)
        Log.d("SO", "Started animation: " + if (animationType == EXPANDING) "Expanding" else "Collapsing")
        card_arrow.startAnimation(arrowAnimation)
        isExpanded = animationType == EXPANDING

    }

    fun setOnExpandedListener(listener: OnExpandedListener) {
        this.listener = listener
    }

    fun setOnCustomClickListener(listener: OnClickListener) {
        this.clickListener = listener
    }

    fun setOnExpandedListener(method: (v: View?, isExpanded: Boolean) -> Unit) {
        this.listener = object : OnExpandedListener {
            override fun onExpandChanged(v: View?, isExpanded: Boolean) {
                method(v, isExpanded)
            }
        }
    }

    fun removeOnExpandedListener() {
        this.listener = null
    }

    fun setTitle(@StringRes titleRes: Int = -1, titleText: String = "") {
        if (titleRes != -1)
            card_title.setText(titleRes)
        else
            card_title.text = titleText
    }

    fun setIcon(@DrawableRes drawableRes: Int = -1, drawable: Drawable? = null) {
        if (drawableRes != -1) {
            iconDrawable = ContextCompat.getDrawable(context, drawableRes)
            card_icon.background = iconDrawable
        } else {
            card_icon.background = drawable
            iconDrawable = drawable
        }

    }

    private fun setInnerView(resId: Int) {
        card_stub.layoutResource = resId
        innerView = card_stub.inflate()
    }


    override fun setOnClickListener(l: View.OnClickListener?) {
        card_arrow.setOnClickListener(l)
        super.setOnClickListener(l)
    }

    private fun convertDpToPixels(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }


    /**
     * Interfaces
     */

    interface OnExpandedListener {

        fun onExpandChanged(v: View?, isExpanded: Boolean)

    }

    interface OnClickListener {

        fun onClicked(isExpanded: Boolean)

    }

    companion object {

        private const val DEFAULT_ANIM_DURATION = 750

        private const val COLLAPSING = 0
        private const val EXPANDING = 1
    }

}


