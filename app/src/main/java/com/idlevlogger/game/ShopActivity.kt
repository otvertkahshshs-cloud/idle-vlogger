package com.idlevlogger.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.idlevlogger.game.databinding.ActivityShopBinding

class ShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopBinding
    private lateinit var adapter: UpgradeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UpgradeAdapter(GameManager.upgrades) { upgradeId ->
            if (GameManager.buyUpgrade(upgradeId)) {
                adapter.notifyDataSetChanged()
                updateMoneyHeader()
            }
        }

        binding.rvUpgrades.layoutManager = LinearLayoutManager(this)
        binding.rvUpgrades.adapter = adapter
        updateMoneyHeader()
    }

    private fun updateMoneyHeader() {
        binding.tvShopMoney.text = "💰 ${GameManager.formatNumber(GameManager.state.money)}"
    }
}

class UpgradeAdapter(
    private val upgrades: List<Upgrade>,
    private val onBuy: (String) -> Unit
) : RecyclerView.Adapter<UpgradeAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvUpgradeName)
        val tvDesc: TextView = view.findViewById(R.id.tvUpgradeDesc)
        val tvLevel: TextView = view.findViewById(R.id.tvUpgradeLevel)
        val tvCost: TextView = view.findViewById(R.id.tvUpgradeCost)
        val btnBuy: Button = view.findViewById(R.id.btnBuy)
        val pbUpgrade: ProgressBar = view.findViewById(R.id.pbUpgrade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upgrade, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val upgrade = upgrades[position]
        val level = GameManager.getLevel(upgrade.id)
        val cost = GameManager.getCost(upgrade.id)
        val canAfford = GameManager.canAfford(upgrade.id)

        holder.tvName.text = upgrade.name
        holder.tvDesc.text = upgrade.description
        holder.tvLevel.text = "Уровень $level"
        holder.tvCost.text = "💰 ${GameManager.formatNumber(cost)}"
        holder.btnBuy.isEnabled = canAfford
        holder.btnBuy.text = if (canAfford) "Купить" else "Мало"
        holder.btnBuy.setOnClickListener { onBuy(upgrade.id) }
        // Progress bar: show level progress up to 10, then cap
        holder.pbUpgrade.progress = ((level % 10) * 10).coerceIn(0, 100)
    }

    override fun getItemCount() = upgrades.size
}
